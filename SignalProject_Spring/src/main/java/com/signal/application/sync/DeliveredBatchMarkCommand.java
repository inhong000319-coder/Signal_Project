package com.signal.application.sync;

import java.util.List;

public final class DeliveredBatchMarkCommand {
    private final Long userId;
    private final Long conversationId;
    private final List<Long> messageIds;

    public DeliveredBatchMarkCommand(Long userId, Long conversationId, List<Long> messageIds) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.messageIds = messageIds;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public List<Long> getMessageIds() {
        return messageIds;
    }
}
