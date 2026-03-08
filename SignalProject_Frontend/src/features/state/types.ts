import type { MessageStateRecord } from "@/types/domain";

export interface MessageStateStoreState {
  byConversation: Record<number, Record<string, MessageStateRecord>>;
}
