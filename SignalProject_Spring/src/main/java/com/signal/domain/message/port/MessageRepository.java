package com.signal.domain.message.port;

import java.util.Optional;

import com.signal.domain.message.Message;

public interface MessageRepository {
    Message save(Message message);
    Optional<Message> findBySenderAndClientKey(Long senderUserId, String clientMessageKey);
    java.util.List<Message> findPage(Long conversationId, Long beforeMessageId, int limit);
    java.util.List<Message> findAfter(Long conversationId, Long afterMessageId, int limit);
    Optional<Message> findById(Long messageId);
    Optional<Message> findLatestByConversationId(Long conversationId);
    long countUnread(Long conversationId, Long userId, Long lastReadMessageId);
    java.util.List<Message> findByIds(java.util.List<Long> messageIds);
}
