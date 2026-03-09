package com.signal.infrastructure.conversation;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConversationMemberJpaRepository extends JpaRepository<ConversationMemberEntity, ConversationMemberId> {
    boolean existsByIdConversationIdAndIdUserId(Long conversationId, Long userId);

    @Query("select m.id.conversationId from ConversationMemberEntity m where m.id.userId = :userId")
    List<Long> findConversationIds(@Param("userId") Long userId, Pageable pageable);

    @Query("select m.id.conversationId from ConversationMemberEntity m " +
        "left join MessageEntity msg on msg.conversationId = m.id.conversationId " +
        "where m.id.userId = :userId " +
        "group by m.id.conversationId " +
        "order by max(msg.messageId) desc")
    List<Long> findConversationIdsOrderByLastMessage(@Param("userId") Long userId, Pageable pageable);
}
