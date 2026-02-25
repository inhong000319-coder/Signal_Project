package com.signal.infrastructure.message;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.signal.domain.message.MessageStateType;

@Entity
@Table(name = "message_states")
public class MessageStateEntity {
    @EmbeddedId
    private MessageStateId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private MessageStateType state;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MessageStateEntity() {
    }

    public MessageStateEntity(MessageStateId id, MessageStateType state, Instant createdAt) {
        this.id = id;
        this.state = state;
        this.createdAt = createdAt;
    }

    public MessageStateId getId() {
        return id;
    }

    public MessageStateType getState() {
        return state;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
