package com.signal.domain.conversation;

import java.util.Objects;

public final class ConversationMember {
    private final Long conversationId;
    private final Long userId;
    private final ConversationMemberRole role;

    private ConversationMember(Long conversationId, Long userId, ConversationMemberRole role) {
        this.conversationId = Objects.requireNonNull(conversationId, "conversationId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.role = Objects.requireNonNull(role, "role");
    }

    public static ConversationMember create(Long conversationId, Long userId, ConversationMemberRole role) {
        return new ConversationMember(conversationId, userId, role);
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public ConversationMemberRole getRole() {
        return role;
    }
}
