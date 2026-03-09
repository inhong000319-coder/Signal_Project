package com.signal.infrastructure.conversation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.signal.domain.conversation.ConversationType;

public interface ConversationJpaRepository extends JpaRepository<ConversationEntity, Long> {
    @Query("select c from ConversationEntity c " +
        "join ConversationMemberEntity m1 on m1.id.conversationId = c.conversationId " +
        "join ConversationMemberEntity m2 on m2.id.conversationId = c.conversationId " +
        "where c.type = :type and m1.id.userId = :a and m2.id.userId = :b")
    Optional<ConversationEntity> findDirectBetween(@Param("type") ConversationType type, @Param("a") Long a, @Param("b") Long b);
}
