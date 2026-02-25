package com.signal.domain.sync.port;

import java.util.Optional;

import com.signal.domain.sync.SyncCursor;

public interface SyncCursorRepository {
    Optional<SyncCursor> findByConversationAndUser(Long conversationId, Long userId);
    SyncCursor save(SyncCursor cursor);
    boolean updateLastDelivered(Long conversationId, Long userId, Long lastDeliveredMessageId);
    boolean updateLastRead(Long conversationId, Long userId, Long lastReadMessageId);
}
