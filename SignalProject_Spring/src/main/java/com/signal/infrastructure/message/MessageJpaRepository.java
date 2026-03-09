package com.signal.infrastructure.message;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {
    Optional<MessageEntity> findBySenderUserIdAndClientMessageKey(Long senderUserId, String clientMessageKey);
    List<MessageEntity> findByConversationId(Long conversationId, Pageable pageable);
    List<MessageEntity> findByConversationIdAndMessageIdLessThan(Long conversationId, Long messageId, Pageable pageable);
    List<MessageEntity> findByConversationIdAndMessageIdGreaterThan(Long conversationId, Long messageId, Pageable pageable);
    Optional<MessageEntity> findTopByConversationIdOrderByMessageIdDesc(Long conversationId);
    long countByConversationIdAndSenderUserIdNot(Long conversationId, Long senderUserId);
    long countByConversationIdAndMessageIdGreaterThanAndSenderUserIdNot(Long conversationId, Long messageId, Long senderUserId);
    List<MessageEntity> findByMessageIdIn(List<Long> messageIds);
}
