import type { MessageStoreState } from "@/features/message/types";

export const selectMessagesByConversation = (conversationId: number) => (state: MessageStoreState) =>
  state.byConversation[conversationId]?.items ?? [];

export const selectMessagePageMeta = (conversationId: number) => (state: MessageStoreState) =>
  state.byConversation[conversationId] ?? {
    items: [],
    nextBeforeMessageId: null,
    hasLoadedInitial: false,
    isLoading: false,
    unreadCount: 0,
  };
