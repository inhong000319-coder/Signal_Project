package com.signal.application.conversation;

public final class ConversationSummaryItem {
    private final Long conversationId;
    private final String type;
    private final boolean active;
    private final Long lastMessageId;
    private final String lastMessageContent;
    private final Long lastMessageSenderUserId;
    private final java.time.Instant lastMessageCreatedAt;
    private final long unreadCount;

    public ConversationSummaryItem(
        Long conversationId,
        String type,
        boolean active,
        Long lastMessageId,
        String lastMessageContent,
        Long lastMessageSenderUserId,
        java.time.Instant lastMessageCreatedAt,
        long unreadCount
    ) {
        this.conversationId = conversationId;
        this.type = type;
        this.active = active;
        this.lastMessageId = lastMessageId;
        this.lastMessageContent = lastMessageContent;
        this.lastMessageSenderUserId = lastMessageSenderUserId;
        this.lastMessageCreatedAt = lastMessageCreatedAt;
        this.unreadCount = unreadCount;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public Long getLastMessageId() {
        return lastMessageId;
    }

    public String getLastMessageContent() {
        return lastMessageContent;
    }

    public Long getLastMessageSenderUserId() {
        return lastMessageSenderUserId;
    }

    public java.time.Instant getLastMessageCreatedAt() {
        return lastMessageCreatedAt;
    }

    public long getUnreadCount() {
        return unreadCount;
    }
}
