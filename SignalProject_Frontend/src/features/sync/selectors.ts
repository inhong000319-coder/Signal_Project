import type { SyncStoreState } from "@/features/sync/types";

export const selectConnectionPhase = (state: SyncStoreState) => state.connectionPhase;
export const selectSyncCursorByConversation = (conversationId: number) => (state: SyncStoreState) =>
  state.cursorsByConversation[conversationId] ?? null;
