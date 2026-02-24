package com.signal.domain.conversation;

import java.time.Instant;
import java.util.Objects;

public final class Conversation {
    private final Long conversationId;
    private final ConversationType type;
    private final boolean active;
    private final Instant createdAt;

    private Conversation(Long conversationId, ConversationType type, boolean active, Instant createdAt) {
        this.conversationId = conversationId;
        this.type = Objects.requireNonNull(type, "type");
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Conversation createNew(ConversationType type, Instant createdAt) {
        return new Conversation(null, type, true, createdAt);
    }

    public static Conversation restore(Long conversationId, ConversationType type, boolean active, Instant createdAt) {
        return new Conversation(conversationId, type, active, createdAt);
    }

    public Long getConversationId() {
        return conversationId;
    }

    public ConversationType getType() {
        return type;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
