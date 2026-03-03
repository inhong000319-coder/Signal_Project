import { create } from "zustand";
import type { MessageStateRecord } from "@/types/domain";
import type { MessageStateStoreState } from "@/features/state/types";

interface MessageStateStore extends MessageStateStoreState {
  upsertState: (record: MessageStateRecord) => void;
  replaceConversationStates: (conversationId: number, records: MessageStateRecord[]) => void;
  clearConversationStates: (conversationId: number) => void;
}

function keyOf(record: Pick<MessageStateRecord, "messageId" | "userId">): string {
  return `${record.messageId}:${record.userId}`;
}

const rank = { SENT: 1, DELIVERED: 2, READ: 3 } as const;

export const useMessageStateStore = create<MessageStateStore>((set) => ({
  byConversation: {},
  upsertState: (record) =>
    set((state) => {
      const currentConversation = { ...(state.byConversation[record.conversationId] ?? {}) };
      const current = currentConversation[keyOf(record)];
      if (!current || rank[record.state] >= rank[current.state]) {
        currentConversation[keyOf(record)] = record;
      }
      return {
        byConversation: {
          ...state.byConversation,
          [record.conversationId]: currentConversation,
        },
      };
    }),
  replaceConversationStates: (conversationId, records) =>
    set((state) => {
      const next: Record<string, MessageStateRecord> = { ...(state.byConversation[conversationId] ?? {}) };
      records.forEach((record) => {
        const current = next[keyOf(record)];
        if (!current || rank[record.state] >= rank[current.state]) {
          next[keyOf(record)] = record;
        }
      });
      return { byConversation: { ...state.byConversation, [conversationId]: next } };
    }),
  clearConversationStates: (conversationId) =>
    set((state) => ({ byConversation: { ...state.byConversation, [conversationId]: {} } })),
}));
