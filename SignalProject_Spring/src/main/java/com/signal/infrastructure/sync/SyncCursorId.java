package com.signal.infrastructure.sync;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SyncCursorId implements Serializable {
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected SyncCursorId() {
    }

    public SyncCursorId(Long conversationId, Long userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SyncCursorId that = (SyncCursorId) o;
        return Objects.equals(conversationId, that.conversationId)
            && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }
}
