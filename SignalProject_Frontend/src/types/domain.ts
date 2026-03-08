export type FriendshipStatus = "PENDING" | "ACCEPTED" | "BLOCKED";
export type ConversationType = "DIRECT" | "GROUP";
export type ConversationMemberRole = "OWNER" | "MEMBER";
export type MessageStateType = "SENT" | "DELIVERED" | "READ";

export interface AuthUser {
  userId: number;
  loginId: string;
  nickname: string;
  userCode: string;
}

export interface TokenPair {
  accessToken: string;
  refreshToken: string;
  accessTokenExpiresAt: string;
  refreshTokenExpiresAt: string;
}

export interface FriendSummary {
  friendUserId: number;
  nickname: string;
  userCode: string;
  conversationId: number | null;
}

export interface PendingFriendRequestSummary {
  requesterUserId: number;
  targetUserId: number;
  counterpartUserId: number;
  counterpartNickname: string;
  counterpartUserCode: string;
  requestedAt: string;
}

export interface ConversationSummary {
  conversationId: number;
  type: ConversationType;
  active: boolean;
  roomName: string | null;
  lastMessageId: number | null;
  lastMessageContent: string | null;
  lastMessageSenderUserId: number | null;
  lastMessageCreatedAt: string | null;
  unreadCount: number;
}

export interface ConversationDetail {
  conversationId: number;
  type: ConversationType;
  active: boolean;
  roomName: string | null;
  createdAt: string;
}

export interface ConversationMemberSummary {
  userId: number;
  nickname: string;
  userCode: string;
  role: ConversationMemberRole;
}

export interface Message {
  messageId: number;
  conversationId: number;
  senderUserId: number;
  content: string;
  clientMessageKey: string;
  createdAt: string;
}

export interface MessageStateRecord {
  conversationId: number;
  messageId: number;
  userId: number;
  state: MessageStateType;
  occurredAt: string;
}

export interface SyncCursorRecord {
  conversationId: number;
  userId: number;
  lastDeliveredMessageId: number | null;
  lastReadMessageId: number | null;
}

export interface ReconnectStateItem {
  messageId: number;
  userId: number;
  state: MessageStateType;
}

export interface ReconnectResult {
  conversationId: number;
  userId: number;
  effectiveLastDeliveredMessageId: number | null;
  effectiveLastReadMessageId: number | null;
  serverLastDeliveredMessageId: number | null;
  serverLastReadMessageId: number | null;
  messages: Message[];
  states: ReconnectStateItem[];
}
