package com.signal.domain.message;

import java.time.Instant;
import java.util.Objects;

public final class Message {
    private final Long messageId;
    private final Long conversationId;
    private final Long senderUserId;
    private final String content;
    private final String clientMessageKey;
    private final Instant createdAt;

    private Message(Long messageId, Long conversationId, Long senderUserId, String content, String clientMessageKey, Instant createdAt) {
        this.messageId = messageId;
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId");
        this.senderUserId = Objects.requireNonNull(senderUserId, "senderUserId");
        this.content = Objects.requireNonNull(content, "content");
        this.clientMessageKey = Objects.requireNonNull(clientMessageKey, "clientMessageKey");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static Message createNew(Long conversationId, Long senderUserId, String content, String clientMessageKey, Instant createdAt) {
        return new Message(null, conversationId, senderUserId, content, clientMessageKey, createdAt);
    }

    public static Message restore(Long messageId, Long conversationId, Long senderUserId, String content, String clientMessageKey, Instant createdAt) {
        return new Message(messageId, conversationId, senderUserId, content, clientMessageKey, createdAt);
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
