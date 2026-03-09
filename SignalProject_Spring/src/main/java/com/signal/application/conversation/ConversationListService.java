package com.signal.application.conversation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
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
public class ConversationListService implements ConversationListUseCase {
    private static final int MAX_LIMIT = 100;
    private static final int DEFAULT_LIMIT = 50;

    private final ConversationMemberRepository conversationMemberRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SyncCursorRepository syncCursorRepository;

    public ConversationListService(
        ConversationMemberRepository conversationMemberRepository,
        ConversationRepository conversationRepository,
        MessageRepository messageRepository,
        SyncCursorRepository syncCursorRepository
    ) {
        this.conversationMemberRepository = conversationMemberRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.syncCursorRepository = syncCursorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationListResult list(ConversationListQuery query) {
        validate(query);
        int limit = normalizeLimit(query.getLimit());

        List<Long> conversationIds = conversationMemberRepository.findConversationIdsByUserId(query.getUserId(), limit);
        List<ConversationSummaryItem> items = new ArrayList<>();

        for (Long conversationId : conversationIds) {
            Conversation conversation = conversationRepository.findById(conversationId)
                .orElse(null);
            if (conversation == null) {
                continue;
            }

            Message lastMessage = messageRepository.findLatestByConversationId(conversationId).orElse(null);
            SyncCursor cursor = syncCursorRepository.findByConversationAndUser(conversationId, query.getUserId())
                .orElse(null);

            long unreadCount = messageRepository.countUnread(
                conversationId,
                query.getUserId(),
                cursor == null ? null : cursor.getLastReadMessageId()
            );

            items.add(new ConversationSummaryItem(
                conversation.getConversationId(),
                conversation.getType().name(),
                conversation.isActive(),
                lastMessage == null ? null : lastMessage.getMessageId(),
                lastMessage == null ? null : lastMessage.getContent(),
                lastMessage == null ? null : lastMessage.getSenderUserId(),
                lastMessage == null ? null : lastMessage.getCreatedAt(),
                unreadCount
            ));
        }

        items.sort(Comparator.comparing(ConversationSummaryItem::getLastMessageCreatedAt,
            Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        return new ConversationListResult(items);
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private void validate(ConversationListQuery query) {
        if (query == null || query.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
    }
}
