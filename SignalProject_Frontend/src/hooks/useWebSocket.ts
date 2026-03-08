import { useCallback } from "react";
import { useAuthStore } from "@/features/auth/store";
import { useConversationStore } from "@/features/conversation/store";
import { useMessageStore } from "@/features/message/store";
import { useMessageStateStore } from "@/features/state/store";
import { useSyncStore } from "@/features/sync/store";
import { signalWebSocketClient } from "@/websocket/client";
import type { ServerEventFrame } from "@/types/frame";

const activeSubscriptions = new Map<number, { count: number; unsubscribe: () => void }>();
let phaseBridgeInitialized = false;

function handleServerFrame(frame: ServerEventFrame): void {
  if (frame.type === "MESSAGE_SENT") {
    useMessageStore.getState().mergeMessages(frame.payload.conversationId, [frame.payload]);
    const activeId = useConversationStore.getState().activeConversationId;
    if (activeId !== frame.payload.conversationId) {
      const messageStore = useMessageStore.getState();
      const current = messageStore.byConversation[frame.payload.conversationId]?.unreadCount ?? 0;
      messageStore.setPageMeta(frame.payload.conversationId, { unreadCount: current + 1 });
    }
    return;
  }

  useMessageStateStore.getState().upsertState(frame.payload);
  const me = useAuthStore.getState().me;
  if (me && frame.payload.userId === me.userId) {
    if (frame.type === "MESSAGE_READ") {
      useSyncStore.getState().markReadCursor(frame.payload.conversationId, me.userId, frame.payload.messageId);
    } else {
      useSyncStore.getState().markDeliveredCursor(frame.payload.conversationId, me.userId, frame.payload.messageId);
    }
  }
}

export function useWebSocket() {
  const connect = useCallback((accessToken?: string | null) => {
    if (!phaseBridgeInitialized) {
      phaseBridgeInitialized = true;
      signalWebSocketClient.onPhaseChange((phase) => {
        useSyncStore.getState().setConnectionPhase(phase);
      });
    }
    signalWebSocketClient.connect(accessToken);
  }, []);

  const disconnect = useCallback(() => {
    activeSubscriptions.clear();
    signalWebSocketClient.disconnect();
  }, []);

  const subscribeConversation = useCallback((conversationId: number) => {
    const existing = activeSubscriptions.get(conversationId);
    if (existing) {
      existing.count += 1;
      return () => {
        const current = activeSubscriptions.get(conversationId);
        if (!current) return;
        current.count -= 1;
        if (current.count <= 0) {
          current.unsubscribe();
          activeSubscriptions.delete(conversationId);
        }
      };
    }

    const unsubscribe = signalWebSocketClient.subscribeConversation(conversationId, handleServerFrame);
    activeSubscriptions.set(conversationId, { count: 1, unsubscribe });

    return () => {
      const current = activeSubscriptions.get(conversationId);
      if (!current) return;
      current.count -= 1;
      if (current.count <= 0) {
        current.unsubscribe();
        activeSubscriptions.delete(conversationId);
      }
    };
  }, []);

  return {
    connect,
    disconnect,
    subscribeConversation,
    getPhase: signalWebSocketClient.getPhase.bind(signalWebSocketClient),
  };
}
