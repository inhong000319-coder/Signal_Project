package com.signal.infrastructure.message;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.signal.domain.message.MessageStateType;

public interface MessageStateJpaRepository extends JpaRepository<MessageStateEntity, MessageStateId> {
    @Query("select m from MessageStateEntity m where m.id.messageId = :messageId and m.id.userId = :userId")
    Optional<MessageStateEntity> findOne(@Param("messageId") Long messageId, @Param("userId") Long userId);

    @Query("select m from MessageStateEntity m where m.id.messageId in :messageIds and m.id.userId = :userId")
    List<MessageStateEntity> findByMessageIdsAndUser(@Param("messageIds") List<Long> messageIds, @Param("userId") Long userId);

    @Modifying
    @Query("update MessageStateEntity m set m.state = :state, m.createdAt = :createdAt where m.id.messageId = :messageId and m.id.userId = :userId")
    int updateState(@Param("messageId") Long messageId, @Param("userId") Long userId, @Param("state") MessageStateType state, @Param("createdAt") java.time.Instant createdAt);
}
