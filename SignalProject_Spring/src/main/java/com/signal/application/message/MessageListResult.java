package com.signal.application.message;

public final class MessageListResult {
    private final java.util.List<MessageListItem> messages;
    private final Long nextBeforeMessageId;
    private final Long lastDeliveredMessageId;
    private final Long lastReadMessageId;
    private final long unreadCount;

    public MessageListResult(
        java.util.List<MessageListItem> messages,
        Long nextBeforeMessageId,
        Long lastDeliveredMessageId,
        Long lastReadMessageId,
        long unreadCount
    ) {
        this.messages = messages;
        this.nextBeforeMessageId = nextBeforeMessageId;
        this.lastDeliveredMessageId = lastDeliveredMessageId;
        this.lastReadMessageId = lastReadMessageId;
        this.unreadCount = unreadCount;
    }

    public java.util.List<MessageListItem> getMessages() {
        return messages;
    }

    public Long getNextBeforeMessageId() {
        return nextBeforeMessageId;
    }

    public Long getLastDeliveredMessageId() {
        return lastDeliveredMessageId;
    }

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public long getUnreadCount() {
        return unreadCount;
    }
}
