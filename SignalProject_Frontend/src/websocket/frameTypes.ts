export type ConnectionPhase =
  | "DISCONNECTED"
  | "CONNECTING"
  | "CONNECTED"
  | "SYNCING"
  | "READY";

export const SERVER_EVENT_TYPES = {
  messageSent: "MESSAGE_SENT",
  messageDelivered: "MESSAGE_DELIVERED",
  messageRead: "MESSAGE_READ",
} as const;
