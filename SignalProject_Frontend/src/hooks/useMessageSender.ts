import { useCallback, useState } from "react";
import { conversationApi } from "@/features/conversation/api";
import { useAuthStore } from "@/features/auth/store";
import { useMessageStore } from "@/features/message/store";
import { newClientMessageKey } from "@/lib/uuid";

export function useMessageSender(conversationId: number | null) {
  const [isSending, setIsSending] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const send = useCallback(
    async (content: string) => {
      if (!conversationId) throw new Error("대화방이 선택되지 않았습니다.");
      const accessToken = useAuthStore.getState().tokenPair?.accessToken;
      if (!accessToken) throw new Error("로그인이 필요합니다.");

      setIsSending(true);
      setError(null);
      try {
        const message = await conversationApi.sendMessage(accessToken, {
          conversationId,
          content,
          clientMessageKey: newClientMessageKey(),
        });
        // WHY: REST command ACK is authoritative message creation result; do not infer from UI local state.
        useMessageStore.getState().mergeMessages(conversationId, [message]);
        return message;
      } catch (e) {
        const message = (e as { message?: string }).message ?? "메시지 전송 실패";
        setError(message);
        throw e;
      } finally {
        setIsSending(false);
      }
    },
    [conversationId]
  );

  return { send, isSending, error };
}
