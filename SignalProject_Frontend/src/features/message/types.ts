import type { Message } from "@/types/domain";

export interface MessagePageState {
  items: Message[];
  nextBeforeMessageId: number | null;
  hasLoadedInitial: boolean;
  isLoading: boolean;
  unreadCount: number;
}

export interface MessageStoreState {
  byConversation: Record<number, MessagePageState>;
}
