package com.signal.domain.message;

import java.time.Instant;
import java.util.Objects;

public final class MessageState {
    private final Long messageId;
    private final Long userId;
    private final MessageStateType state;
    private final Instant createdAt;

    private MessageState(Long messageId, Long userId, MessageStateType state, Instant createdAt) {
        this.messageId = Objects.requireNonNull(messageId, "messageId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.state = Objects.requireNonNull(state, "state");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static MessageState sent(Long messageId, Long userId, Instant createdAt) {
        return new MessageState(messageId, userId, MessageStateType.SENT, createdAt);
    }

    public static MessageState restore(Long messageId, Long userId, MessageStateType state, Instant createdAt) {
        return new MessageState(messageId, userId, state, createdAt);
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public MessageStateType getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
