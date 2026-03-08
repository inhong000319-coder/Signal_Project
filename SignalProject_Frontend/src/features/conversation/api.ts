import { httpClient } from "@/lib/httpClient";
import type {
  AddConversationMembersRequestDto,
  BatchMarkRequestDto,
  ConversationListResponseDto,
  ConversationMemberListResponseDto,
  CreateDirectConversationRequestDto,
  CreateGroupConversationRequestDto,
  MarkResponseDto,
  MessageListResponseDto,
  ReconnectRequestDto,
  ReconnectResponseDto,
  SendMessageRequestDto,
  SingleMarkRequestDto,
} from "@/features/conversation/types";
import type { ConversationDetail, Message } from "@/types/domain";

function authHeader(accessToken: string): Record<string, string> {
  return { Authorization: `Bearer ${accessToken}` };
}

export const conversationApi = {
  list(accessToken: string) {
    return httpClient.get<ConversationListResponseDto>("/api/conversations", { headers: authHeader(accessToken) });
  },
  createDirect(accessToken: string, payload: CreateDirectConversationRequestDto) {
    return httpClient.post<ConversationDetail>("/api/conversations/direct", payload, { headers: authHeader(accessToken) });
  },
  createGroup(accessToken: string, payload: CreateGroupConversationRequestDto) {
    return httpClient.post<ConversationDetail>("/api/conversations/group", payload, { headers: authHeader(accessToken) });
  },
  leave(accessToken: string, conversationId: number) {
    return httpClient.post<void>(`/api/conversations/${conversationId}/leave`, undefined, {
      headers: authHeader(accessToken),
    });
  },
  deleteConversation(accessToken: string, conversationId: number) {
    return httpClient.post<void>(`/api/conversations/${conversationId}/delete`, undefined, {
      headers: authHeader(accessToken),
    });
  },
  listMembers(accessToken: string, conversationId: number) {
    return httpClient.get<ConversationMemberListResponseDto>(`/api/conversations/${conversationId}/members`, {
      headers: authHeader(accessToken),
    });
  },
  addMembers(accessToken: string, conversationId: number, payload: AddConversationMembersRequestDto) {
    return httpClient.post<ConversationMemberListResponseDto>(`/api/conversations/${conversationId}/members`, payload, {
      headers: authHeader(accessToken),
    });
  },
  listMessages(accessToken: string, params: { conversationId: number; beforeMessageId?: number | null; limit: number }) {
    const query = new URLSearchParams();
    query.set("conversationId", String(params.conversationId));
    query.set("limit", String(params.limit));
    if (params.beforeMessageId != null) query.set("beforeMessageId", String(params.beforeMessageId));
    return httpClient.get<MessageListResponseDto>(`/api/messages?${query.toString()}`, { headers: authHeader(accessToken) });
  },
  sendMessage(accessToken: string, payload: SendMessageRequestDto) {
    return httpClient.post<Message>("/api/messages", payload, { headers: authHeader(accessToken) });
  },
  markRead(accessToken: string, payload: SingleMarkRequestDto) {
    return httpClient.post<MarkResponseDto>("/api/read", payload, { headers: authHeader(accessToken) });
  },
  markReadBatch(accessToken: string, payload: BatchMarkRequestDto) {
    return httpClient.post<MarkResponseDto>("/api/read/batch", payload, { headers: authHeader(accessToken) });
  },
  markDelivered(accessToken: string, payload: SingleMarkRequestDto) {
    return httpClient.post<MarkResponseDto>("/api/read/delivered", payload, { headers: authHeader(accessToken) });
  },
  markDeliveredBatch(accessToken: string, payload: BatchMarkRequestDto) {
    return httpClient.post<MarkResponseDto>("/api/read/delivered/batch", payload, { headers: authHeader(accessToken) });
  },
  reconnectSync(accessToken: string, payload: ReconnectRequestDto) {
    return httpClient.post<ReconnectResponseDto>("/api/read/reconnect", payload, { headers: authHeader(accessToken) });
  },
};