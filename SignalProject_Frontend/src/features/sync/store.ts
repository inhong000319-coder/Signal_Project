import { create } from "zustand";
import type { SyncCursorRecord } from "@/types/domain";
import type { SyncStoreState } from "@/features/sync/types";
import type { ConnectionPhase } from "@/websocket/frameTypes";

interface SyncStore extends SyncStoreState {
  upsertCursor: (cursor: SyncCursorRecord) => void;
  setConnectionPhase: (phase: ConnectionPhase) => void;
  setSyncingConversationId: (conversationId: number | null) => void;
  markDeliveredCursor: (conversationId: number, userId: number, messageId: number) => void;
  markReadCursor: (conversationId: number, userId: number, messageId: number) => void;
}

function upsertMonotonic(current: SyncCursorRecord | undefined, next: SyncCursorRecord): SyncCursorRecord {
  if (!current) return next;
  return {
    conversationId: next.conversationId,
    userId: next.userId,
    lastDeliveredMessageId: Math.max(current.lastDeliveredMessageId ?? 0, next.lastDeliveredMessageId ?? 0) || null,
    lastReadMessageId: Math.max(current.lastReadMessageId ?? 0, next.lastReadMessageId ?? 0) || null,
  };
}

export const useSyncStore = create<SyncStore>((set) => ({
  cursorsByConversation: {},
  connectionPhase: "DISCONNECTED",
  syncingConversationId: null,
  upsertCursor: (cursor) =>
    set((state) => ({
      cursorsByConversation: {
        ...state.cursorsByConversation,
        [cursor.conversationId]: upsertMonotonic(state.cursorsByConversation[cursor.conversationId], cursor),
      },
    })),
  setConnectionPhase: (connectionPhase) => set({ connectionPhase }),
  setSyncingConversationId: (syncingConversationId) => set({ syncingConversationId }),
  markDeliveredCursor: (conversationId, userId, messageId) =>
    set((state) => {
      const current = state.cursorsByConversation[conversationId] ?? {
        conversationId,
        userId,
        lastDeliveredMessageId: null,
        lastReadMessageId: null,
      };
      return {
        cursorsByConversation: {
          ...state.cursorsByConversation,
          [conversationId]: {
            ...current,
            lastDeliveredMessageId: Math.max(current.lastDeliveredMessageId ?? 0, messageId),
          },
        },
      };
    }),
  markReadCursor: (conversationId, userId, messageId) =>
    set((state) => {
      const current = state.cursorsByConversation[conversationId] ?? {
        conversationId,
        userId,
        lastDeliveredMessageId: null,
        lastReadMessageId: null,
      };
      const maxDelivered = Math.max(current.lastDeliveredMessageId ?? 0, messageId);
      const maxRead = Math.max(current.lastReadMessageId ?? 0, messageId);
      return {
        cursorsByConversation: {
          ...state.cursorsByConversation,
          [conversationId]: {
            ...current,
            lastDeliveredMessageId: maxDelivered,
            lastReadMessageId: maxRead,
          },
        },
      };
    }),
}));
