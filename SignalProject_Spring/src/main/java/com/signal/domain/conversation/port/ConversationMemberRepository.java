package com.signal.domain.conversation.port;

public interface ConversationMemberRepository {
    void saveAll(java.util.List<com.signal.domain.conversation.ConversationMember> members);
    boolean exists(Long conversationId, Long userId);
    java.util.List<Long> findConversationIdsByUserId(Long userId, int limit);
}
