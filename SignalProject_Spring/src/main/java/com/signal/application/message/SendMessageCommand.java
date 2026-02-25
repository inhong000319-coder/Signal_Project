package com.signal.application.message;

public final class SendMessageCommand {
    private final Long senderUserId;
    private final Long conversationId;
    private final String content;
    private final String clientMessageKey;

    public SendMessageCommand(Long senderUserId, Long conversationId, String content, String clientMessageKey) {
        this.senderUserId = senderUserId;
        this.conversationId = conversationId;
        this.content = content;
        this.clientMessageKey = clientMessageKey;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getContent() {
        return content;
    }

    public String getClientMessageKey() {
        return clientMessageKey;
    }
}
