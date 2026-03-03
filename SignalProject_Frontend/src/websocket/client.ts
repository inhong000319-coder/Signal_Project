import { Client, type IFrame, type IMessage, type StompSubscription } from "@stomp/stompjs";
import { topicForConversation, WS_ENDPOINT } from "@/lib/constants";
import type { ServerEventFrame } from "@/types/frame";
import { ConnectionStateMachine } from "@/websocket/connectionStateMachine";
import type { ConnectionPhase } from "@/websocket/frameTypes";
import { mapTopicPayloadToServerFrame } from "@/websocket/frameMapper";

export interface SignalWebSocketClient {
  connect(accessToken?: string | null): void;
  disconnect(): void;
  subscribeConversation(conversationId: number, listener: (frame: ServerEventFrame) => void): () => void;
  onPhaseChange(listener: (phase: ConnectionPhase) => void): () => void;
  setSyncing(): void;
  setReady(): void;
  getPhase(): ConnectionPhase;
}

class StompSignalWebSocketClient implements SignalWebSocketClient {
  private machine = new ConnectionStateMachine();
  private phaseListeners = new Set<(phase: ConnectionPhase) => void>();
  private conversationListeners = new Map<number, Set<(frame: ServerEventFrame) => void>>();
  private topicSubscriptions = new Map<string, StompSubscription>();
  private client: Client | null = null;
  private activeToken: string | null = null;
  private connectVersion = 0;

  connect(accessToken?: string | null): void {
    if (!accessToken) {
      this.disconnect();
      return;
    }

    if (this.activeToken === accessToken && this.client?.active) {
      return;
    }

    this.activeToken = accessToken;
    const version = ++this.connectVersion;
    void this.reconnect(version, accessToken);
  }

  disconnect(): void {
    this.connectVersion += 1;
    this.activeToken = null;
    this.clearTopicSubscriptions();

    const client = this.client;
    this.client = null;
    if (client) {
      void client.deactivate();
    }

    this.conversationListeners.clear();
    this.notify(this.machine.reset());
  }

  subscribeConversation(conversationId: number, listener: (frame: ServerEventFrame) => void): () => void {
    const listeners = this.conversationListeners.get(conversationId) ?? new Set();
    listeners.add(listener);
    this.conversationListeners.set(conversationId, listeners);

    this.ensureConversationSubscribed(conversationId);

    return () => {
      const current = this.conversationListeners.get(conversationId);
      if (!current) return;

      current.delete(listener);
      if (current.size === 0) {
        this.conversationListeners.delete(conversationId);
        this.unsubscribeConversationTopics(conversationId);
      }
    };
  }

  onPhaseChange(listener: (phase: ConnectionPhase) => void): () => void {
    this.phaseListeners.add(listener);
    listener(this.machine.getPhase());
    return () => this.phaseListeners.delete(listener);
  }

  setSyncing(): void {
    if (this.machine.getPhase() === "CONNECTED" || this.machine.getPhase() === "READY") {
      this.notify(this.machine.transition("SYNCING"));
    }
  }

  setReady(): void {
    if (this.machine.getPhase() === "SYNCING") {
      this.notify(this.machine.transition("READY"));
    }
  }

  getPhase(): ConnectionPhase {
    return this.machine.getPhase();
  }

  private async reconnect(version: number, accessToken: string): Promise<void> {
    const previous = this.client;
    this.client = null;

    if (previous) {
      this.clearTopicSubscriptions();
      try {
        await previous.deactivate();
      } catch {
        // ignore shutdown errors during reconnect churn
      }
    }

    if (version !== this.connectVersion || this.activeToken !== accessToken) {
      return;
    }

    this.notify(this.machine.reset());
    this.notify(this.machine.transition("CONNECTING"));

    const client = new Client({
      brokerURL: buildBrokerUrl(),
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      debug: () => {
        // WHY: STOMP debug logs are noisy in normal app usage; keep transport silent by default.
      },
    });

    client.onConnect = () => {
      if (version !== this.connectVersion || this.client !== client) {
        void client.deactivate();
        return;
      }

      this.notify(this.machine.transition("CONNECTED"));
      this.resubscribeAll();
    };

    client.onStompError = (frame) => {
      if (this.client !== client) return;
      logStompError(frame);
      this.clearTopicSubscriptions();
      this.notify(this.machine.reset());
    };

    client.onWebSocketClose = () => {
      if (this.client !== client) return;
      this.clearTopicSubscriptions();
      this.notify(this.machine.reset());
    };

    client.onWebSocketError = () => {
      if (this.client !== client) return;
      this.clearTopicSubscriptions();
      this.notify(this.machine.reset());
    };

    this.client = client;
    client.activate();
  }

  private ensureConversationSubscribed(conversationId: number): void {
    if (!this.client?.connected) {
      return;
    }

    const topics = topicForConversation(conversationId);
    this.subscribeTopic(topics.messages);
    this.subscribeTopic(topics.delivered);
    this.subscribeTopic(topics.reads);
  }

  private unsubscribeConversationTopics(conversationId: number): void {
    const topics = topicForConversation(conversationId);
    [topics.messages, topics.delivered, topics.reads].forEach((topic) => {
      const subscription = this.topicSubscriptions.get(topic);
      if (!subscription) return;
      subscription.unsubscribe();
      this.topicSubscriptions.delete(topic);
    });
  }

  private subscribeTopic(topic: string): void {
    if (!this.client?.connected) {
      return;
    }

    if (this.topicSubscriptions.has(topic)) {
      return;
    }

    const subscription = this.client.subscribe(topic, (message) => {
      this.handleBrokerMessage(topic, message);
    });

    this.topicSubscriptions.set(topic, subscription);
  }

  private handleBrokerMessage(topic: string, message: IMessage): void {
    if (!message.body) {
      return;
    }

    let payload: unknown;
    try {
      payload = JSON.parse(message.body);
    } catch {
      return;
    }

    const frame = mapTopicPayloadToServerFrame(topic, payload);
    if (!frame) {
      return;
    }

    const listeners = this.conversationListeners.get(frame.payload.conversationId);
    listeners?.forEach((listener) => listener(frame));
  }

  private resubscribeAll(): void {
    this.clearTopicSubscriptions();
    this.conversationListeners.forEach((_listeners, conversationId) => {
      this.ensureConversationSubscribed(conversationId);
    });
  }

  private clearTopicSubscriptions(): void {
    this.topicSubscriptions.forEach((subscription) => subscription.unsubscribe());
    this.topicSubscriptions.clear();
  }

  private notify(phase: ConnectionPhase): void {
    this.phaseListeners.forEach((listener) => listener(phase));
  }
}

interface StompErrorBody {
  code?: string;
  message?: string;
}

function parseErrorBody(body?: string): StompErrorBody | null {
  if (!body || !body.trim()) {
    return null;
  }
  try {
    const parsed = JSON.parse(body) as StompErrorBody;
    return typeof parsed === "object" && parsed !== null ? parsed : null;
  } catch {
    return null;
  }
}

function logStompError(frame?: IFrame): void {
  const fallbackMessage = frame?.headers?.message ?? "stomp error";
  const parsed = parseErrorBody(frame?.body);
  const code = parsed?.code ?? "WS_STOMP_ERROR";
  const message = parsed?.message ?? fallbackMessage;
  console.warn(`[WebSocket] ${code}: ${message}`);
}

function buildBrokerUrl(): string {
  if (typeof window === "undefined") {
    return "ws://127.0.0.1:18080/ws";
  }

  const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
  return `${protocol}//${window.location.host}${WS_ENDPOINT}`;
}

export const signalWebSocketClient: SignalWebSocketClient = new StompSignalWebSocketClient();
