package com.signal.domain.sync;

import java.util.Objects;

public final class SyncCursor {
    private final Long conversationId;
    private final Long userId;
    private final Long lastDeliveredMessageId;
    private final Long lastReadMessageId;

    private SyncCursor(Long conversationId, Long userId, Long lastDeliveredMessageId, Long lastReadMessageId) {
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.lastDeliveredMessageId = lastDeliveredMessageId;
        this.lastReadMessageId = lastReadMessageId;
    }

    public static SyncCursor create(Long conversationId, Long userId, Long lastDeliveredMessageId, Long lastReadMessageId) {
        return new SyncCursor(conversationId, userId, lastDeliveredMessageId, lastReadMessageId);
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

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }
}
