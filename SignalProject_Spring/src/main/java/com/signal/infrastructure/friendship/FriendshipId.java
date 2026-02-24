package com.signal.infrastructure.friendship;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FriendshipId implements Serializable {
    @Column(name = "requester_user_id", nullable = false)
    private Long requesterUserId;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    protected FriendshipId() {
    }

    public FriendshipId(Long requesterUserId, Long targetUserId) {
        this.requesterUserId = requesterUserId;
        this.targetUserId = targetUserId;
    }

    public Long getRequesterUserId() {
        return requesterUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FriendshipId that = (FriendshipId) o;
        return Objects.equals(requesterUserId, that.requesterUserId)
            && Objects.equals(targetUserId, that.targetUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requesterUserId, targetUserId);
    }
}
