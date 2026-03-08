import { useCallback } from "react";
import { RECONNECT_SYNC_LIMIT } from "@/lib/constants";
import { conversationApi } from "@/features/conversation/api";
import { useAuthStore } from "@/features/auth/store";
import { useMessageStore } from "@/features/message/store";
import { useMessageStateStore } from "@/features/state/store";
import { mapReconnectStatesToRecords } from "@/features/state/selectors";
import { useSyncStore } from "@/features/sync/store";
import { signalWebSocketClient } from "@/websocket/client";

export function useReconnectSync() {
  return useCallback(async (conversationId: number) => {
    const auth = useAuthStore.getState();
    const me = auth.me;
    const accessToken = auth.tokenPair?.accessToken;
    if (!me || !accessToken) return null;

    const cursor = useSyncStore.getState().cursorsByConversation[conversationId] ?? {
      conversationId,
      userId: me.userId,
      lastDeliveredMessageId: null,
      lastReadMessageId: null,
    };

    signalWebSocketClient.setSyncing();
    useSyncStore.getState().setSyncingConversationId(conversationId);

    try {
      const response = await conversationApi.reconnectSync(accessToken, {
        conversationId,
        clientLastDeliveredMessageId: cursor.lastDeliveredMessageId,
        clientLastReadMessageId: cursor.lastReadMessageId,
        limit: RECONNECT_SYNC_LIMIT,
      });

      useMessageStore.getState().mergeMessages(conversationId, response.messages);
      useMessageStateStore.getState().replaceConversationStates(
        conversationId,
        mapReconnectStatesToRecords(conversationId, response.states)
      );
      useSyncStore.getState().upsertCursor({
        conversationId,
        userId: response.userId,
        lastDeliveredMessageId: response.serverLastDeliveredMessageId,
        lastReadMessageId: response.serverLastReadMessageId,
      });

      return response;
    } finally {
      useSyncStore.getState().setSyncingConversationId(null);
      signalWebSocketClient.setReady();
    }
  }, []);
}
