package com.signal.application.message;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.signal.application.message.event.MessageSentEvent;
import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.port.ConversationMemberRepository;
import com.signal.domain.conversation.port.ConversationRepository;
import com.signal.domain.message.Message;
import com.signal.domain.message.MessageState;
import com.signal.domain.message.port.MessageRepository;
import com.signal.domain.message.port.MessageStateRepository;

@Service
public class MessageSendService implements MessageSendUseCase {
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageStateRepository messageStateRepository;
    private final ClockHolder clockHolder;
    private final ApplicationEventPublisher eventPublisher;

    public MessageSendService(
        ConversationRepository conversationRepository,
        ConversationMemberRepository conversationMemberRepository,
        MessageRepository messageRepository,
        MessageStateRepository messageStateRepository,
        ClockHolder clockHolder,
        ApplicationEventPublisher eventPublisher
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.messageRepository = messageRepository;
        this.messageStateRepository = messageStateRepository;
        this.clockHolder = clockHolder;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public SendMessageResult send(SendMessageCommand command) {
        validate(command);

        Conversation conversation = conversationRepository.findById(command.getConversationId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CONVERSATION_NOT_FOUND, "conversation not found"));
        if (!conversation.isActive()) {
            throw new BusinessException(ErrorCode.CONVERSATION_INACTIVE, "conversation inactive");
        }

        boolean member = conversationMemberRepository.exists(command.getConversationId(), command.getSenderUserId());
        if (!member) {
            throw new BusinessException(ErrorCode.NOT_MEMBER, "sender not in conversation");
        }

        Message created = Message.createNew(
            command.getConversationId(),
            command.getSenderUserId(),
            command.getContent(),
            command.getClientMessageKey(),
            clockHolder.now()
        );

        Message saved;
        try {
            saved = messageRepository.save(created);
        } catch (DataIntegrityViolationException ex) {
            // Idempotency via unique(sender_user_id, client_message_key)
            saved = messageRepository.findBySenderAndClientKey(command.getSenderUserId(), command.getClientMessageKey())
                .orElseThrow(() -> new BusinessException(ErrorCode.DUPLICATE_MESSAGE_KEY, "duplicate message"));
            return toResult(saved);
        }

        messageStateRepository.save(MessageState.sent(saved.getMessageId(), saved.getSenderUserId(), clockHolder.now()));

        eventPublisher.publishEvent(new MessageSentEvent(
            saved.getMessageId(),
            saved.getConversationId(),
            saved.getSenderUserId(),
            saved.getContent(),
            saved.getClientMessageKey(),
            saved.getCreatedAt()
        ));

        return toResult(saved);
    }

    private void validate(SendMessageCommand command) {
        if (command == null || command.getSenderUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (command.getConversationId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "conversationId required");
        }
        if (!StringUtils.hasText(command.getContent())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "content required");
        }
        if (!StringUtils.hasText(command.getClientMessageKey())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "clientMessageKey required");
        }
    }

    private SendMessageResult toResult(Message message) {
        return new SendMessageResult(
            message.getMessageId(),
            message.getConversationId(),
            message.getSenderUserId(),
            message.getContent(),
            message.getClientMessageKey(),
            message.getCreatedAt()
        );
    }
}
