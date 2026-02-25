package com.signal.application.sync;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.conversation.port.ConversationMemberRepository;
import com.signal.domain.message.Message;
import com.signal.domain.message.MessageState;
import com.signal.domain.message.port.MessageRepository;
import com.signal.domain.message.port.MessageStateRepository;
import com.signal.domain.sync.SyncCursor;
import com.signal.domain.sync.port.SyncCursorRepository;

@Service
public class ReconnectService implements ReconnectUseCase {
    private static final int MAX_LIMIT = 200;
    private static final int DEFAULT_LIMIT = 50;

    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageRepository messageRepository;
    private final MessageStateRepository messageStateRepository;
    private final SyncCursorRepository syncCursorRepository;

    public ReconnectService(
        ConversationMemberRepository conversationMemberRepository,
        MessageRepository messageRepository,
        MessageStateRepository messageStateRepository,
        SyncCursorRepository syncCursorRepository
    ) {
        this.conversationMemberRepository = conversationMemberRepository;
        this.messageRepository = messageRepository;
        this.messageStateRepository = messageStateRepository;
        this.syncCursorRepository = syncCursorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ReconnectResult reconnect(ReconnectQuery query) {
        validate(query);

        boolean member = conversationMemberRepository.exists(query.getConversationId(), query.getUserId());
        if (!member) {
            throw new BusinessException(ErrorCode.NOT_MEMBER, "user not in conversation");
        }

        SyncCursor serverCursor = syncCursorRepository.findByConversationAndUser(query.getConversationId(), query.getUserId())
            .orElse(null);

        Long serverLastDelivered = serverCursor == null ? null : serverCursor.getLastDeliveredMessageId();
        Long serverLastRead = serverCursor == null ? null : serverCursor.getLastReadMessageId();

        Long effectiveLastDelivered = max(serverLastDelivered, query.getClientLastDeliveredMessageId());
        Long effectiveLastRead = max(serverLastRead, query.getClientLastReadMessageId());

        int limit = normalizeLimit(query.getLimit());
        List<Message> messages = messageRepository.findAfter(query.getConversationId(), effectiveLastDelivered, limit);

        List<Long> messageIds = messages.stream().map(Message::getMessageId).toList();
        List<MessageState> states = messageIds.isEmpty()
            ? List.of()
            : messageStateRepository.findByMessageIdsAndUser(messageIds, query.getUserId());

        Map<Long, MessageState> stateMap = states.stream()
            .collect(Collectors.toMap(MessageState::getMessageId, s -> s));

        List<ReconnectMessageItem> items = messages.stream()
            .map(m -> new ReconnectMessageItem(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ))
            .toList();

        List<ReconnectStateItem> stateItems = messages.stream()
            .map(m -> {
                MessageState state = stateMap.get(m.getMessageId());
                return new ReconnectStateItem(
                    m.getMessageId(),
                    query.getUserId(),
                    state == null ? null : state.getState().name()
                );
            })
            .toList();

        return new ReconnectResult(
            query.getConversationId(),
            query.getUserId(),
            effectiveLastDelivered,
            effectiveLastRead,
            serverLastDelivered,
            serverLastRead,
            items,
            stateItems
        );
    }

    private void validate(ReconnectQuery query) {
        if (query == null || query.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (query.getConversationId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "conversationId required");
        }
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private Long max(Long a, Long b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return Math.max(a, b);
    }
}
