package com.signal.application.sync;

public final class DeliveredMarkResult {
    private final Long conversationId;
    private final Long userId;
    private final Long lastDeliveredMessageId;

    public DeliveredMarkResult(Long conversationId, Long userId, Long lastDeliveredMessageId) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.lastDeliveredMessageId = lastDeliveredMessageId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getLastDeliveredMessageId() {
        return lastDeliveredMessageId;
    }
}
