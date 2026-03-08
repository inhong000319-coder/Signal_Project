import { useCallback } from "react";
import { PAGE_LIMIT } from "@/lib/constants";
import { conversationApi } from "@/features/conversation/api";
import { useAuthStore } from "@/features/auth/store";
import { useConversationStore } from "@/features/conversation/store";
import { useMessageStore } from "@/features/message/store";
import { useMessageStateStore } from "@/features/state/store";
import { useSyncStore } from "@/features/sync/store";

function requireAccessToken(): string {
  const accessToken = useAuthStore.getState().tokenPair?.accessToken;
  if (!accessToken) throw new Error("로그인이 필요합니다.");
  return accessToken;
}

export function useConversation() {
  const conversationStore = useConversationStore();

  const loadConversations = useCallback(async () => {
    const accessToken = requireAccessToken();
    const conversationState = useConversationStore.getState();

    conversationState.setLoading(true);
    conversationState.setError(null);
    try {
      const response = await conversationApi.list(accessToken);
      conversationState.setConversations(response.conversations);
      return response.conversations;
    } catch (error) {
      conversationState.setError((error as { message?: string }).message ?? "대화 목록을 불러오지 못했습니다.");
      throw error;
    } finally {
      conversationState.setLoading(false);
    }
  }, []);

  const createDirectConversation = useCallback(async (targetUserId: number) => {
    const accessToken = requireAccessToken();
    const detail = await conversationApi.createDirect(accessToken, { targetUserId });

    const conversationState = useConversationStore.getState();
    conversationState.setActiveConversationDetail(detail);
    conversationState.setActiveConversationId(detail.conversationId);

    await loadConversations();
    return detail;
  }, [loadConversations]);

  const createGroupConversation = useCallback(async (memberUserIds: number[], roomName?: string | null) => {
    const accessToken = requireAccessToken();
    const detail = await conversationApi.createGroup(accessToken, {
      memberUserIds,
      roomName: roomName?.trim() ? roomName.trim() : null,
    });

    const conversationState = useConversationStore.getState();
    conversationState.setActiveConversationDetail(detail);
    conversationState.setActiveConversationId(detail.conversationId);

    await loadConversations();
    return detail;
  }, [loadConversations]);

  const leaveConversation = useCallback(async (conversationId: number) => {
    const accessToken = requireAccessToken();
    await conversationApi.leave(accessToken, conversationId);

    useMessageStore.getState().resetConversation(conversationId);
    useMessageStateStore.getState().clearConversationStates(conversationId);
    const conversationState = useConversationStore.getState();
    conversationState.setActiveConversationId(null);
    conversationState.setActiveConversationDetail(null);

    await loadConversations();
  }, [loadConversations]);

  const deleteConversation = useCallback(async (conversationId: number) => {
    const accessToken = requireAccessToken();
    await conversationApi.deleteConversation(accessToken, conversationId);

    useMessageStore.getState().resetConversation(conversationId);
    useMessageStateStore.getState().clearConversationStates(conversationId);
    const conversationState = useConversationStore.getState();
    conversationState.setActiveConversationId(null);
    conversationState.setActiveConversationDetail(null);

    await loadConversations();
  }, [loadConversations]);

  const loadConversationMembers = useCallback(async (conversationId: number) => {
    const accessToken = requireAccessToken();
    return conversationApi.listMembers(accessToken, conversationId);
  }, []);

  const addMembersToConversation = useCallback(async (conversationId: number, memberUserIds: number[]) => {
    const accessToken = requireAccessToken();
    return conversationApi.addMembers(accessToken, conversationId, { memberUserIds });
  }, []);

  const openConversation = useCallback(async (conversationId: number) => {
    const conversationState = useConversationStore.getState();
    const messageState = useMessageStore.getState();

    conversationState.setActiveConversationId(conversationId);
    const accessToken = requireAccessToken();
    messageState.setLoading(conversationId, true);
    try {
      const response = await conversationApi.listMessages(accessToken, {
        conversationId,
        beforeMessageId: null,
        limit: PAGE_LIMIT,
      });
      messageState.mergeMessages(conversationId, response.messages);
      messageState.setPageMeta(conversationId, {
        nextBeforeMessageId: response.nextBeforeMessageId,
        hasLoadedInitial: true,
        unreadCount: response.unreadCount,
      });

      const me = useAuthStore.getState().me;
      if (me) {
        useSyncStore.getState().upsertCursor({
          conversationId,
          userId: me.userId,
          lastDeliveredMessageId: response.lastDeliveredMessageId,
          lastReadMessageId: response.lastReadMessageId,
        });
      }

      return response;
    } finally {
      messageState.setLoading(conversationId, false);
    }
  }, []);

  const loadOlderMessages = useCallback(async (conversationId: number) => {
    const accessToken = requireAccessToken();
    const meta = useMessageStore.getState().byConversation[conversationId];
    if (!meta?.nextBeforeMessageId) return null;

    const messageState = useMessageStore.getState();
    messageState.setLoading(conversationId, true);
    try {
      const response = await conversationApi.listMessages(accessToken, {
        conversationId,
        beforeMessageId: meta.nextBeforeMessageId,
        limit: PAGE_LIMIT,
      });
      messageState.mergeMessages(conversationId, response.messages);
      messageState.setPageMeta(conversationId, {
        nextBeforeMessageId: response.nextBeforeMessageId,
        unreadCount: response.unreadCount,
      });
      return response;
    } finally {
      messageState.setLoading(conversationId, false);
    }
  }, []);

  const markDeliveredBatch = useCallback(async (conversationId: number, messageIds: number[]) => {
    if (messageIds.length == 0) return null;
    const accessToken = requireAccessToken();
    const result = await conversationApi.markDeliveredBatch(accessToken, { conversationId, messageIds });
    const me = useAuthStore.getState().me;
    if (me) {
      useSyncStore.getState().upsertCursor({
        conversationId,
        userId: me.userId,
        lastDeliveredMessageId: result.lastDeliveredMessageId ?? null,
        lastReadMessageId: useSyncStore.getState().cursorsByConversation[conversationId]?.lastReadMessageId ?? null,
      });
    }
    return result;
  }, []);

  const markReadBatch = useCallback(async (conversationId: number, messageIds: number[]) => {
    if (messageIds.length == 0) return null;
    const accessToken = requireAccessToken();
    const result = await conversationApi.markReadBatch(accessToken, { conversationId, messageIds });
    const me = useAuthStore.getState().me;
    if (me) {
      const current = useSyncStore.getState().cursorsByConversation[conversationId];
      useSyncStore.getState().upsertCursor({
        conversationId,
        userId: me.userId,
        lastDeliveredMessageId: Math.max(current?.lastDeliveredMessageId ?? 0, result.lastReadMessageId ?? 0) || null,
        lastReadMessageId: result.lastReadMessageId ?? null,
      });
    }
    return result;
  }, []);

  return {
    ...conversationStore,
    loadConversations,
    createDirectConversation,
    createGroupConversation,
    leaveConversation,
    deleteConversation,
    loadConversationMembers,
    addMembersToConversation,
    openConversation,
    loadOlderMessages,
    markDeliveredBatch,
    markReadBatch,
  };
}