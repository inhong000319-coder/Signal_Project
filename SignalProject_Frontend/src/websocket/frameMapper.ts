import type { Message, MessageStateRecord } from "@/types/domain";
import type { ServerEventFrame } from "@/types/frame";

export function mapTopicPayloadToServerFrame(topic: string, payload: unknown): ServerEventFrame | null {
  if (topic.endsWith("/messages")) {
    return {
      type: "MESSAGE_SENT",
      payload: payload as Message,
    };
  }

  if (topic.endsWith("/delivered")) {
    const delivered = payload as { conversationId: number; userId: number; messageId: number; occurredAt?: string };
    if (typeof delivered.occurredAt !== "string" || delivered.occurredAt.length === 0) {
      return null;
    }

    const state: MessageStateRecord = {
      conversationId: delivered.conversationId,
      userId: delivered.userId,
      messageId: delivered.messageId,
      state: "DELIVERED",
      occurredAt: delivered.occurredAt,
    };

    return { type: "MESSAGE_DELIVERED", payload: state };
  }

  if (topic.endsWith("/reads")) {
    const read = payload as { conversationId: number; userId: number; messageId: number; occurredAt?: string };
    if (typeof read.occurredAt !== "string" || read.occurredAt.length === 0) {
      return null;
    }

    const state: MessageStateRecord = {
      conversationId: read.conversationId,
      userId: read.userId,
      messageId: read.messageId,
      state: "READ",
      occurredAt: read.occurredAt,
    };

    return { type: "MESSAGE_READ", payload: state };
  }

  return null;
}
