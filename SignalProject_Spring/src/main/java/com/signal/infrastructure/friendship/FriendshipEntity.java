package com.signal.infrastructure.friendship;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.signal.domain.friendship.FriendshipStatus;

@Entity
@Table(name = "friendships")
public class FriendshipEntity {
    @EmbeddedId
    private FriendshipId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendshipStatus status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "accepted_at")
    private Instant acceptedAt;

    protected FriendshipEntity() {
    }

    public FriendshipEntity(FriendshipId id, FriendshipStatus status, Instant requestedAt, Instant acceptedAt) {
        this.id = id;
        this.status = status;
        this.requestedAt = requestedAt;
        this.acceptedAt = acceptedAt;
    }

    public FriendshipId getId() {
        return id;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }

    public void updateStatus(FriendshipStatus status, Instant acceptedAt) {
        this.status = status;
        this.acceptedAt = acceptedAt;
    }
}
