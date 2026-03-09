package com.signal.application.sync.event;

import java.time.Instant;

public final class MessageDeliveredEvent {
    private final Long conversationId;
    private final Long userId;
    private final Long messageId;
    private final Instant occurredAt;

    public MessageDeliveredEvent(Long conversationId, Long userId, Long messageId, Instant occurredAt) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.messageId = messageId;
        this.occurredAt = occurredAt;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
