package com.signal.application.conversation;

import java.time.Instant;

public final class ConversationResult {
    private final Long conversationId;
    private final String type;
    private final boolean active;
    private final Instant createdAt;

    public ConversationResult(Long conversationId, String type, boolean active, Instant createdAt) {
        this.conversationId = conversationId;
        this.type = type;
        this.active = active;
        this.createdAt = createdAt;
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

    public Instant getCreatedAt() {
        return createdAt;
    }
}
