import type { Message, MessageStateRecord } from "@/types/domain";

export interface SendMessageFrame {
  type: "SEND_MESSAGE";
  conversationId: number;
  content: string;
  clientMessageKey: string;
}

export interface MessageDeliveredFrame {
  type: "MESSAGE_DELIVERED";
  messageId: number;
}

export interface MessageReadFrame {
  type: "MESSAGE_READ";
  messageId: number;
}

export interface ReconnectSyncFrame {
  type: "RECONNECT_SYNC";
  conversationId: number;
  lastDeliveredMessageId: number;
  lastReadMessageId: number;
}

export type ClientCommandFrame =
  | SendMessageFrame
  | MessageDeliveredFrame
  | MessageReadFrame
  | ReconnectSyncFrame;

export interface MessageSentEventFrame {
  type: "MESSAGE_SENT";
  payload: Message;
}

export interface MessageDeliveredEventFrame {
  type: "MESSAGE_DELIVERED";
  payload: MessageStateRecord;
}

export interface MessageReadEventFrame {
  type: "MESSAGE_READ";
  payload: MessageStateRecord;
}

export type ServerEventFrame =
  | MessageSentEventFrame
  | MessageDeliveredEventFrame
  | MessageReadEventFrame;
