package com.signal.application.conversation;

public final class ConversationListQuery {
    private final Long userId;
    private final int limit;

    public ConversationListQuery(Long userId, int limit) {
        this.userId = userId;
        this.limit = limit;
    }

    public Long getUserId() {
        return userId;
    }

    public int getLimit() {
        return limit;
    }
}
