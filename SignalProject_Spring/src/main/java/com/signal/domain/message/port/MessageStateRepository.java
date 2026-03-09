package com.signal.domain.message.port;

import java.util.Optional;

import com.signal.domain.message.MessageState;

public interface MessageStateRepository {
    void save(MessageState messageState);
    Optional<MessageState> findByMessageAndUser(Long messageId, Long userId);
    void updateState(Long messageId, Long userId, com.signal.domain.message.MessageStateType state, java.time.Instant createdAt);
    java.util.List<MessageState> findByMessageIdsAndUser(java.util.List<Long> messageIds, Long userId);
}
