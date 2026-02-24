package com.signal.application.friendship;

public final class FriendAcceptCommand {
    private final Long requesterUserId;
    private final Long targetUserId;

    public FriendAcceptCommand(Long requesterUserId, Long targetUserId) {
        this.requesterUserId = requesterUserId;
        this.targetUserId = targetUserId;
    }

    public Long getRequesterUserId() {
        return requesterUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }
}
