package com.signal.application.sync;

import java.time.Instant;

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
public class ReadMarkService implements ReadMarkUseCase {
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageStateRepository messageStateRepository;
    private final SyncCursorRepository syncCursorRepository;
    private final ClockHolder clockHolder;
    private final ApplicationEventPublisher eventPublisher;

    public ReadMarkService(
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
    public ReadMarkResult markRead(ReadMarkCommand command) {
        validate(command);

        boolean member = conversationMemberRepository.exists(command.getConversationId(), command.getUserId());
        if (!member) {
            throw new BusinessException(ErrorCode.NOT_MEMBER, "user not in conversation");
        }

        Message message = messageRepository.findById(command.getMessageId())
            .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_NOT_FOUND, "message not found"));

        if (!message.getConversationId().equals(command.getConversationId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "message does not belong to conversation");
        }

        if (message.getSenderUserId().equals(command.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "sender cannot mark read");
        }

        Instant now = clockHolder.now();

        MessageState existing = messageStateRepository.findByMessageAndUser(command.getMessageId(), command.getUserId())
            .orElse(null);

        if (existing == null) {
            messageStateRepository.save(MessageState.restore(
                command.getMessageId(), command.getUserId(), MessageStateType.READ, now
            ));
        } else if (existing.getState() != MessageStateType.READ) {
            messageStateRepository.updateState(command.getMessageId(), command.getUserId(), MessageStateType.READ, now);
        }

        boolean updated = syncCursorRepository.updateLastRead(command.getConversationId(), command.getUserId(), command.getMessageId());
        if (!updated) {
            throw new BusinessException(ErrorCode.SYNC_CURSOR_CONFLICT, "cursor update failed");
        }

        eventPublisher.publishEvent(new MessageReadEvent(
            command.getConversationId(),
            command.getUserId(),
            command.getMessageId(),
            now
        ));

        return new ReadMarkResult(command.getConversationId(), command.getUserId(), command.getMessageId());
    }

    private void validate(ReadMarkCommand command) {
        if (command == null || command.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (command.getConversationId() == null || command.getMessageId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "conversationId/messageId required");
        }
    }
}
