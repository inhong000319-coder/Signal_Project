package com.signal.application.sync;

public final class ReconnectQuery {
    private final Long userId;
    private final Long conversationId;
    private final Long clientLastDeliveredMessageId;
    private final Long clientLastReadMessageId;
    private final int limit;

    public ReconnectQuery(
        Long userId,
        Long conversationId,
        Long clientLastDeliveredMessageId,
        Long clientLastReadMessageId,
        int limit
    ) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.clientLastDeliveredMessageId = clientLastDeliveredMessageId;
        this.clientLastReadMessageId = clientLastReadMessageId;
        this.limit = limit;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getClientLastDeliveredMessageId() {
        return clientLastDeliveredMessageId;
    }

    public Long getClientLastReadMessageId() {
        return clientLastReadMessageId;
    }

    public int getLimit() {
        return limit;
    }
}
