import type { ConversationDetail, ConversationMemberSummary, ConversationSummary, Message, ReconnectResult } from "@/types/domain";

export interface ConversationListResponseDto {
  conversations: ConversationSummary[];
}

export interface CreateDirectConversationRequestDto {
  targetUserId: number;
}

export interface CreateGroupConversationRequestDto {
  memberUserIds: number[];
  roomName?: string | null;
}

export interface AddConversationMembersRequestDto {
  memberUserIds: number[];
}

export interface ConversationMemberListResponseDto {
  conversationId: number;
  members: ConversationMemberSummary[];
}

export interface MessageListResponseDto {
  messages: Message[];
  nextBeforeMessageId: number | null;
  lastDeliveredMessageId: number | null;
  lastReadMessageId: number | null;
  unreadCount: number;
}

export interface SendMessageRequestDto {
  conversationId: number;
  content: string;
  clientMessageKey: string;
}

export interface SingleMarkRequestDto {
  conversationId: number;
  messageId: number;
}

export interface BatchMarkRequestDto {
  conversationId: number;
  messageIds: number[];
}

export interface ReconnectRequestDto {
  conversationId: number;
  clientLastDeliveredMessageId: number | null;
  clientLastReadMessageId: number | null;
  limit?: number;
}

export interface MarkResponseDto {
  conversationId: number;
  userId: number;
  lastDeliveredMessageId?: number | null;
  lastReadMessageId?: number | null;
}

export interface ConversationStoreState {
  conversations: ConversationSummary[];
  activeConversationId: number | null;
  activeConversationDetail: ConversationDetail | null;
  isLoading: boolean;
  error: string | null;
}

export type ReconnectResponseDto = ReconnectResult;
