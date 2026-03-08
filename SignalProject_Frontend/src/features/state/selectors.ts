import type { MessageStateStoreState } from "@/features/state/types";
import type { MessageStateRecord, MessageStateType } from "@/types/domain";

export const selectConversationStates = (conversationId: number) => (state: MessageStateStoreState) =>
  state.byConversation[conversationId] ?? {};

export const selectHighestStateForMessage =
  (conversationId: number, messageId: number): ((state: MessageStateStoreState) => MessageStateType | null) =>
  (state) => {
    const records = Object.values(state.byConversation[conversationId] ?? {}).filter((r) => r.messageId === messageId);
    if (records.some((r) => r.state === "READ")) return "READ";
    if (records.some((r) => r.state === "DELIVERED")) return "DELIVERED";
    if (records.some((r) => r.state === "SENT")) return "SENT";
    return null;
  };

export const mapReconnectStatesToRecords = (
  conversationId: number,
  states: Array<{ messageId: number; userId: number; state: MessageStateType | null | undefined }>
): MessageStateRecord[] =>
  states
    .filter((item): item is { messageId: number; userId: number; state: MessageStateType } =>
      item.state === "SENT" || item.state === "DELIVERED" || item.state === "READ"
    )
    .map((item) => ({
      conversationId,
      messageId: item.messageId,
      userId: item.userId,
      state: item.state,
      // WHY: reconnect state snapshot payload omits occurredAt; avoid inventing client timestamps.
      occurredAt: "",
    }));
