package com.signal.application.conversation;

public final class CreateDirectConversationCommand {
    private final Long requesterUserId;
    private final Long targetUserId;

    public CreateDirectConversationCommand(Long requesterUserId, Long targetUserId) {
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
