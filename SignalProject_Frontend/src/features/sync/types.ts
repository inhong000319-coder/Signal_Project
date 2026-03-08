import type { SyncCursorRecord } from "@/types/domain";
import type { ConnectionPhase } from "@/websocket/frameTypes";

export interface SyncStoreState {
  cursorsByConversation: Record<number, SyncCursorRecord>;
  connectionPhase: ConnectionPhase;
  syncingConversationId: number | null;
}
