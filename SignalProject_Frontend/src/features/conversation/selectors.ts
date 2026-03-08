import type { ConversationStoreState } from "@/features/conversation/types";

export const selectConversationList = (state: ConversationStoreState) => state.conversations;
export const selectActiveConversationId = (state: ConversationStoreState) => state.activeConversationId;
