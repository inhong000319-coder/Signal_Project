package com.signal.domain.friendship;

import java.time.Instant;
import java.util.Objects;

public final class Friendship {
    private final Long requesterUserId;
    private final Long targetUserId;
    private final FriendshipStatus status;
    private final Instant requestedAt;
    private final Instant acceptedAt;

    private Friendship(Long requesterUserId, Long targetUserId, FriendshipStatus status, Instant requestedAt, Instant acceptedAt) {
        this.requesterUserId = Objects.requireNonNull(requesterUserId, "requesterUserId");
        this.targetUserId = Objects.requireNonNull(targetUserId, "targetUserId");
        this.status = Objects.requireNonNull(status, "status");
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt");
        this.acceptedAt = acceptedAt;
    }

    public static Friendship request(Long requesterUserId, Long targetUserId, Instant requestedAt) {
        return new Friendship(requesterUserId, targetUserId, FriendshipStatus.PENDING, requestedAt, null);
    }

    public static Friendship accept(Long requesterUserId, Long targetUserId, Instant requestedAt, Instant acceptedAt) {
        return new Friendship(requesterUserId, targetUserId, FriendshipStatus.ACCEPTED, requestedAt, acceptedAt);
    }

    public static Friendship block(Long requesterUserId, Long targetUserId, Instant requestedAt) {
        return new Friendship(requesterUserId, targetUserId, FriendshipStatus.BLOCKED, requestedAt, null);
    }

    public static Friendship restore(Long requesterUserId, Long targetUserId, FriendshipStatus status, Instant requestedAt, Instant acceptedAt) {
        return new Friendship(requesterUserId, targetUserId, status, requestedAt, acceptedAt);
    }

    public Long getRequesterUserId() {
        return requesterUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
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
}
