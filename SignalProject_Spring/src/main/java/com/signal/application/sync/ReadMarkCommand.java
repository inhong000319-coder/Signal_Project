package com.signal.application.sync;

public final class ReadMarkCommand {
    private final Long userId;
    private final Long conversationId;
    private final Long messageId;

    public ReadMarkCommand(Long userId, Long conversationId, Long messageId) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }
}
