import type { ConnectionPhase } from "@/websocket/frameTypes";

const allowed: Record<ConnectionPhase, ConnectionPhase[]> = {
  DISCONNECTED: ["CONNECTING"],
  CONNECTING: ["CONNECTED", "DISCONNECTED"],
  CONNECTED: ["SYNCING", "DISCONNECTED"],
  SYNCING: ["READY", "DISCONNECTED"],
  READY: ["SYNCING", "DISCONNECTED"],
};

export class ConnectionStateMachine {
  private phase: ConnectionPhase = "DISCONNECTED";

  getPhase(): ConnectionPhase {
    return this.phase;
  }

  transition(next: ConnectionPhase): ConnectionPhase {
    if (!allowed[this.phase].includes(next)) {
      return this.phase;
    }

    this.phase = next;
    return this.phase;
  }

  reset(): ConnectionPhase {
    this.phase = "DISCONNECTED";
    return this.phase;
  }
}
