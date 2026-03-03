import { create } from "zustand";
import type { ConversationDetail, ConversationSummary } from "@/types/domain";
import type { ConversationStoreState } from "@/features/conversation/types";

interface ConversationStore extends ConversationStoreState {
  setConversations: (items: ConversationSummary[]) => void;
  upsertConversation: (item: ConversationSummary) => void;
  setActiveConversationId: (conversationId: number | null) => void;
  setActiveConversationDetail: (detail: ConversationDetail | null) => void;
  setLoading: (isLoading: boolean) => void;
  setError: (error: string | null) => void;
}

export const useConversationStore = create<ConversationStore>((set) => ({
  conversations: [],
  activeConversationId: null,
  activeConversationDetail: null,
  isLoading: false,
  error: null,
  setConversations: (items) => set({ conversations: items, error: null }),
  upsertConversation: (item) =>
    set((state) => {
      const conversations = state.conversations.filter((c) => c.conversationId !== item.conversationId);
      conversations.push(item);
      conversations.sort((a, b) => (b.lastMessageId ?? 0) - (a.lastMessageId ?? 0));
      return { conversations };
    }),
  setActiveConversationId: (activeConversationId) => set({ activeConversationId }),
  setActiveConversationDetail: (activeConversationDetail) => set({ activeConversationDetail }),
  setLoading: (isLoading) => set({ isLoading }),
  setError: (error) => set({ error }),
}));
