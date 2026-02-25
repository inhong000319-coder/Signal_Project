package com.signal.application.message.event;

import java.time.Instant;

public final class MessageSentEvent {
    private final Long messageId;
    private final Long conversationId;
    private final Long senderUserId;
    private final String content;
    private final String clientMessageKey;
    private final Instant createdAt;

    public MessageSentEvent(
        Long messageId,
        Long conversationId,
        Long senderUserId,
        String content,
        String clientMessageKey,
        Instant createdAt
    ) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderUserId = senderUserId;
        this.content = content;
        this.clientMessageKey = clientMessageKey;
        this.createdAt = createdAt;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public String getContent() {
        return content;
    }

    public String getClientMessageKey() {
        return clientMessageKey;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
