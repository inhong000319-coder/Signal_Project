export const API_BASE_URL = "/api";
export const WS_ENDPOINT = "/ws";

export const STORAGE_KEYS = {
  auth: "signal.auth",
  lastConversationId: "signal.lastConversationId",
} as const;

export const PAGE_LIMIT = 30;
export const RECONNECT_SYNC_LIMIT = 50;

export const topicForConversation = (conversationId: number) => ({
  messages: `/topic/conversations/${conversationId}/messages`,
  delivered: `/topic/conversations/${conversationId}/delivered`,
  reads: `/topic/conversations/${conversationId}/reads`,
});
