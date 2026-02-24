package com.signal.infrastructure.conversation;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import com.signal.domain.conversation.ConversationMemberRole;

@Entity
@Table(name = "conversation_members")
public class ConversationMemberEntity {
    @EmbeddedId
    private ConversationMemberId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private ConversationMemberRole role;

    protected ConversationMemberEntity() {
    }

    public ConversationMemberEntity(ConversationMemberId id, ConversationMemberRole role) {
        this.id = id;
        this.role = role;
    }

    public ConversationMemberId getId() {
        return id;
    }

    public ConversationMemberRole getRole() {
        return role;
    }
}
