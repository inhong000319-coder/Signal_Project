package com.signal.application.friendship;

import java.time.Instant;

public final class FriendshipResult {
    private final Long requesterUserId;
    private final Long targetUserId;
    private final String status;
    private final Instant requestedAt;
    private final Instant acceptedAt;

    public FriendshipResult(Long requesterUserId, Long targetUserId, String status, Instant requestedAt, Instant acceptedAt) {
        this.requesterUserId = requesterUserId;
        this.targetUserId = targetUserId;
        this.status = status;
        this.requestedAt = requestedAt;
        this.acceptedAt = acceptedAt;
    }

    public Long getRequesterUserId() {
        return requesterUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public String getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public Instant getAcceptedAt() {
        return acceptedAt;
    }
}
