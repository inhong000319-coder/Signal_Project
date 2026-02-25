package com.signal.infrastructure.sync;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SyncCursorJpaRepository extends JpaRepository<SyncCursorEntity, SyncCursorId> {
    @Query("select s from SyncCursorEntity s where s.id.conversationId = :conversationId and s.id.userId = :userId")
    Optional<SyncCursorEntity> findOne(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Modifying
    @Query("update SyncCursorEntity s set s.lastDeliveredMessageId = " +
        "case when s.lastDeliveredMessageId is null or s.lastDeliveredMessageId < :value then :value else s.lastDeliveredMessageId end " +
        "where s.id.conversationId = :conversationId and s.id.userId = :userId")
    int updateLastDeliveredMax(
        @Param("conversationId") Long conversationId,
        @Param("userId") Long userId,
        @Param("value") Long value
    );

    @Modifying
    @Query("update SyncCursorEntity s set s.lastReadMessageId = " +
        "case when s.lastReadMessageId is null or s.lastReadMessageId < :value then :value else s.lastReadMessageId end " +
        "where s.id.conversationId = :conversationId and s.id.userId = :userId")
    int updateLastReadMax(
        @Param("conversationId") Long conversationId,
        @Param("userId") Long userId,
        @Param("value") Long value
    );
}
