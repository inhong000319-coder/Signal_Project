package com.signal.infrastructure.sync;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.signal.domain.sync.SyncCursor;
import com.signal.domain.sync.port.SyncCursorRepository;

@Repository
public class SyncCursorRepositoryImpl implements SyncCursorRepository {
    private final SyncCursorJpaRepository syncCursorJpaRepository;

    public SyncCursorRepositoryImpl(SyncCursorJpaRepository syncCursorJpaRepository) {
        this.syncCursorJpaRepository = syncCursorJpaRepository;
    }

    @Override
    public Optional<SyncCursor> findByConversationAndUser(Long conversationId, Long userId) {
        return syncCursorJpaRepository.findOne(conversationId, userId)
            .map(this::toDomain);
    }

    @Override
    public SyncCursor save(SyncCursor cursor) {
        SyncCursorEntity saved = syncCursorJpaRepository.save(
            new SyncCursorEntity(
                new SyncCursorId(cursor.getConversationId(), cursor.getUserId()),
                cursor.getLastDeliveredMessageId(),
                cursor.getLastReadMessageId()
            )
        );
        return toDomain(saved);
    }

    @Override
    public boolean updateLastDelivered(Long conversationId, Long userId, Long lastDeliveredMessageId) {
        int updated = syncCursorJpaRepository.updateLastDeliveredMax(conversationId, userId, lastDeliveredMessageId);
        if (updated > 0) {
            return true;
        }
        try {
            syncCursorJpaRepository.save(new SyncCursorEntity(
                new SyncCursorId(conversationId, userId),
                lastDeliveredMessageId,
                null
            ));
            return true;
        } catch (DataIntegrityViolationException ex) {
            return syncCursorJpaRepository.updateLastDeliveredMax(conversationId, userId, lastDeliveredMessageId) > 0;
        }
    }

    @Override
    public boolean updateLastRead(Long conversationId, Long userId, Long lastReadMessageId) {
        int updated = syncCursorJpaRepository.updateLastReadMax(conversationId, userId, lastReadMessageId);
        if (updated > 0) {
            return true;
        }
        try {
            syncCursorJpaRepository.save(new SyncCursorEntity(
                new SyncCursorId(conversationId, userId),
                null,
                lastReadMessageId
            ));
            return true;
        } catch (DataIntegrityViolationException ex) {
            return syncCursorJpaRepository.updateLastReadMax(conversationId, userId, lastReadMessageId) > 0;
        }
    }

    private SyncCursor toDomain(SyncCursorEntity entity) {
        return SyncCursor.create(
            entity.getId().getConversationId(),
            entity.getId().getUserId(),
            entity.getLastDeliveredMessageId(),
            entity.getLastReadMessageId()
        );
    }
}
