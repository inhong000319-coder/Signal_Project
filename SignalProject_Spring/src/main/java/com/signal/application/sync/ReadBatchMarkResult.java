package com.signal.application.sync;

public final class ReadBatchMarkResult {
    private final Long conversationId;
    private final Long userId;
    private final Long lastReadMessageId;

    public ReadBatchMarkResult(Long conversationId, Long userId, Long lastReadMessageId) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.lastReadMessageId = lastReadMessageId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }
}
