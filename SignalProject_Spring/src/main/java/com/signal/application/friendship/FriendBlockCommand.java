package com.signal.application.friendship;

public final class FriendBlockCommand {
    private final Long blockerUserId;
    private final Long targetUserId;

    public FriendBlockCommand(Long blockerUserId, Long targetUserId) {
        this.blockerUserId = blockerUserId;
        this.targetUserId = targetUserId;
    }

    public Long getBlockerUserId() {
        return blockerUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }
}
