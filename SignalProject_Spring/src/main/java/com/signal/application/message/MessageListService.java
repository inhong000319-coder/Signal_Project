package com.signal.application.message;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.port.ConversationMemberRepository;
import com.signal.domain.conversation.port.ConversationRepository;
import com.signal.domain.message.Message;
import com.signal.domain.message.port.MessageRepository;
import com.signal.domain.sync.SyncCursor;
import com.signal.domain.sync.port.SyncCursorRepository;

@Service
public class MessageListService implements MessageListUseCase {
    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 50;

    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final SyncCursorRepository syncCursorRepository;

    public MessageListService(
        ConversationRepository conversationRepository,
        ConversationMemberRepository conversationMemberRepository,
        MessageRepository messageRepository,
        SyncCursorRepository syncCursorRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.messageRepository = messageRepository;
        this.syncCursorRepository = syncCursorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MessageListResult list(MessageListQuery query) {
        validate(query);

        Conversation conversation = conversationRepository.findById(query.getConversationId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CONVERSATION_NOT_FOUND, "conversation not found"));
        if (!conversation.isActive()) {
            throw new BusinessException(ErrorCode.CONVERSATION_INACTIVE, "conversation inactive");
        }

        boolean member = conversationMemberRepository.exists(query.getConversationId(), query.getUserId());
        if (!member) {
            throw new BusinessException(ErrorCode.NOT_MEMBER, "user not in conversation");
        }

        int limit = normalizeLimit(query.getLimit());
        List<Message> messages = messageRepository.findPage(query.getConversationId(), query.getBeforeMessageId(), limit);

        List<MessageListItem> items = messages.stream()
            .map(m -> new MessageListItem(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ))
            .toList();

        Long nextBefore = items.isEmpty() ? null : items.get(items.size() - 1).getMessageId();

        SyncCursor cursor = syncCursorRepository.findByConversationAndUser(query.getConversationId(), query.getUserId())
            .orElse(null);
        Long lastRead = cursor == null ? null : cursor.getLastReadMessageId();
        Long lastDelivered = cursor == null ? null : cursor.getLastDeliveredMessageId();
        long unreadCount = messageRepository.countUnread(query.getConversationId(), query.getUserId(), lastRead);

        return new MessageListResult(items, nextBefore, lastDelivered, lastRead, unreadCount);
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private void validate(MessageListQuery query) {
        if (query == null || query.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (query.getConversationId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "conversationId required");
        }
    }
}
