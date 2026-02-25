package com.signal.application.sync;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.signal.application.sync.event.MessageReadEvent;
import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.conversation.port.ConversationMemberRepository;
import com.signal.domain.message.Message;
import com.signal.domain.message.MessageState;
import com.signal.domain.message.MessageStateType;
import com.signal.domain.message.port.MessageRepository;
import com.signal.domain.message.port.MessageStateRepository;
import com.signal.domain.sync.port.SyncCursorRepository;

@Service
public class ReadBatchMarkService implements ReadBatchMarkUseCase {
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageStateRepository messageStateRepository;
    private final SyncCursorRepository syncCursorRepository;
    private final ClockHolder clockHolder;
    private final ApplicationEventPublisher eventPublisher;

    public ReadBatchMarkService(
        ConversationMemberRepository conversationMemberRepository,
        MessageRepository messageRepository,
        MessageStateRepository messageStateRepository,
        SyncCursorRepository syncCursorRepository,
        ClockHolder clockHolder,
        ApplicationEventPublisher eventPublisher
    ) {
        this.conversationMemberRepository = conversationMemberRepository;
        this.messageRepository = messageRepository;
        this.messageStateRepository = messageStateRepository;
        this.syncCursorRepository = syncCursorRepository;
        this.clockHolder = clockHolder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ReadBatchMarkResult markReadBatch(ReadBatchMarkCommand command) {
        validate(command);

        boolean member = conversationMemberRepository.exists(command.getConversationId(), command.getUserId());
        if (!member) {
            throw new BusinessException(ErrorCode.NOT_MEMBER, "user not in conversation");
        }

        Instant now = clockHolder.now();
        LinkedHashSet<Long> uniqueIds = new LinkedHashSet<>(command.getMessageIds());
        List<Message> messages = messageRepository.findByIds(List.copyOf(uniqueIds));

        if (messages.size() != uniqueIds.size()) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND, "message not found");
        }

        Map<Long, Message> byId = messages.stream()
            .collect(Collectors.toMap(Message::getMessageId, Function.identity()));

        Long maxMessageId = null;

        for (Long messageId : uniqueIds) {
            Message message = byId.get(messageId);
            if (!message.getConversationId().equals(command.getConversationId())) {
                throw new BusinessException(ErrorCode.INVALID_INPUT, "message does not belong to conversation");
            }

            if (message.getSenderUserId().equals(command.getUserId())) {
                continue; // sender is excluded
            }

            MessageState existing = messageStateRepository.findByMessageAndUser(messageId, command.getUserId())
                .orElse(null);

            if (existing == null) {
                messageStateRepository.save(MessageState.restore(
                    messageId, command.getUserId(), MessageStateType.READ, now
                ));
            } else if (existing.getState() != MessageStateType.READ) {
                messageStateRepository.updateState(messageId, command.getUserId(), MessageStateType.READ, now);
            }

            if (maxMessageId == null || messageId > maxMessageId) {
                maxMessageId = messageId;
            }
        }

        if (maxMessageId != null) {
            boolean updated = syncCursorRepository.updateLastRead(
                command.getConversationId(),
                command.getUserId(),
                maxMessageId
            );
            if (!updated) {
                throw new BusinessException(ErrorCode.SYNC_CURSOR_CONFLICT, "cursor update failed");
            }

            eventPublisher.publishEvent(new MessageReadEvent(
                command.getConversationId(),
                command.getUserId(),
                maxMessageId,
                now
            ));
        }

        return new ReadBatchMarkResult(command.getConversationId(), command.getUserId(), maxMessageId);
    }

    private void validate(ReadBatchMarkCommand command) {
        if (command == null || command.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (command.getConversationId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "conversationId required");
        }
        if (command.getMessageIds() == null || command.getMessageIds().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "messageIds required");
        }
    }
}
