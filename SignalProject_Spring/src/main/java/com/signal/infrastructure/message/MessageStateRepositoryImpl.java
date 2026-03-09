package com.signal.infrastructure.message;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.signal.domain.message.MessageState;
import com.signal.domain.message.MessageStateType;
import com.signal.domain.message.port.MessageStateRepository;

@Repository
public class MessageStateRepositoryImpl implements MessageStateRepository {
    private final MessageStateJpaRepository messageStateJpaRepository;

    public MessageStateRepositoryImpl(MessageStateJpaRepository messageStateJpaRepository) {
        this.messageStateJpaRepository = messageStateJpaRepository;
    }

    @Override
    public void save(MessageState messageState) {
        messageStateJpaRepository.save(
            new MessageStateEntity(
                new MessageStateId(messageState.getMessageId(), messageState.getUserId()),
                messageState.getState(),
                messageState.getCreatedAt()
            )
        );
    }

    @Override
    public Optional<MessageState> findByMessageAndUser(Long messageId, Long userId) {
        return messageStateJpaRepository.findOne(messageId, userId)
            .map(e -> MessageState.restore(e.getId().getMessageId(), e.getId().getUserId(), e.getState(), e.getCreatedAt()));
    }

    @Override
    public void updateState(Long messageId, Long userId, MessageStateType state, Instant createdAt) {
        messageStateJpaRepository.updateState(messageId, userId, state, createdAt);
    }

    @Override
    public List<MessageState> findByMessageIdsAndUser(List<Long> messageIds, Long userId) {
        return messageStateJpaRepository.findByMessageIdsAndUser(messageIds, userId).stream()
            .map(e -> MessageState.restore(e.getId().getMessageId(), e.getId().getUserId(), e.getState(), e.getCreatedAt()))
            .toList();
    }
}
