package com.signal.infrastructure.message;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

    @Column(name = "content", nullable = false, length = 2000)
    private String content;

    @Column(name = "client_message_key", nullable = false, length = 100)
    private String clientMessageKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected MessageEntity() {
    }

    public MessageEntity(Long conversationId, Long senderUserId, String content, String clientMessageKey, Instant createdAt) {
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
