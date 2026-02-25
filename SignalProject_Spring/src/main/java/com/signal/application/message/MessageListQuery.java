package com.signal.application.message;

public final class MessageListQuery {
    private final Long userId;
    private final Long conversationId;
    private final Long beforeMessageId;
    private final int limit;

    public MessageListQuery(Long userId, Long conversationId, Long beforeMessageId, int limit) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.beforeMessageId = beforeMessageId;
        this.limit = limit;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getBeforeMessageId() {
        return beforeMessageId;
    }

    public int getLimit() {
        return limit;
    }
}
