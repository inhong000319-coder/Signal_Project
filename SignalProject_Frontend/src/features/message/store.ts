import { create } from "zustand";
import type { Message } from "@/types/domain";
import type { MessagePageState, MessageStoreState } from "@/features/message/types";

interface MessageStore extends MessageStoreState {
  setLoading: (conversationId: number, isLoading: boolean) => void;
  mergeMessages: (conversationId: number, messages: Message[]) => void;
  setPageMeta: (conversationId: number, meta: Partial<Pick<MessagePageState, "nextBeforeMessageId" | "hasLoadedInitial" | "unreadCount">>) => void;
  resetConversation: (conversationId: number) => void;
}

const emptyPage = (): MessagePageState => ({
  items: [],
  nextBeforeMessageId: null,
  hasLoadedInitial: false,
  isLoading: false,
  unreadCount: 0,
});

export const useMessageStore = create<MessageStore>((set) => ({
  byConversation: {},
  setLoading: (conversationId, isLoading) =>
    set((state) => ({
      byConversation: {
        ...state.byConversation,
        [conversationId]: {
          ...(state.byConversation[conversationId] ?? emptyPage()),
          isLoading,
        },
      },
    })),
  mergeMessages: (conversationId, messages) =>
    set((state) => {
      const current = state.byConversation[conversationId] ?? emptyPage();
      const map = new Map<number, Message>(current.items.map((message) => [message.messageId, message]));
      messages.forEach((message) => map.set(message.messageId, message));
      const items = [...map.values()].sort((a, b) => a.messageId - b.messageId);
      return {
        byConversation: {
          ...state.byConversation,
          [conversationId]: {
            ...current,
            items,
          },
        },
      };
    }),
  setPageMeta: (conversationId, meta) =>
    set((state) => ({
      byConversation: {
        ...state.byConversation,
        [conversationId]: {
          ...(state.byConversation[conversationId] ?? emptyPage()),
          ...meta,
        },
      },
    })),
  resetConversation: (conversationId) =>
    set((state) => ({
      byConversation: {
        ...state.byConversation,
        [conversationId]: emptyPage(),
      },
    })),
}));
