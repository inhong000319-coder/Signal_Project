package com.signal.application.conversation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.ConversationType;
import com.signal.domain.conversation.port.ConversationMemberRepository;
import com.signal.domain.conversation.port.ConversationRepository;
import com.signal.domain.message.Message;
import com.signal.domain.message.port.MessageRepository;
import com.signal.domain.sync.SyncCursor;
import com.signal.domain.sync.port.SyncCursorRepository;

@ExtendWith(MockitoExtension.class)
class ConversationListServiceTest {
    @Mock
    ConversationMemberRepository conversationMemberRepository;

    @Mock
    ConversationRepository conversationRepository;

    @Mock
    MessageRepository messageRepository;

    @Mock
    SyncCursorRepository syncCursorRepository;

    @InjectMocks
    ConversationListService service;

    @Test
    void list_includesUnreadCountFromCursor() {
        Long userId = 1L;
        Long conversationId = 10L;

        when(conversationMemberRepository.findConversationIdsByUserId(userId, 50))
            .thenReturn(List.of(conversationId));

        when(conversationRepository.findById(conversationId))
            .thenReturn(java.util.Optional.of(Conversation.restore(conversationId, ConversationType.DIRECT, true, Instant.now())));

        when(messageRepository.findLatestByConversationId(conversationId))
            .thenReturn(java.util.Optional.of(Message.restore(99L, conversationId, 2L, "hi", "k", Instant.now())));

        when(syncCursorRepository.findByConversationAndUser(conversationId, userId))
            .thenReturn(java.util.Optional.of(SyncCursor.create(conversationId, userId, null, 50L)));

        when(messageRepository.countUnread(conversationId, userId, 50L)).thenReturn(7L);

        ConversationListResult result = service.list(new ConversationListQuery(userId, 50));

        assertThat(result.getConversations()).hasSize(1);
        ConversationSummaryItem item = result.getConversations().get(0);
        assertThat(item.getUnreadCount()).isEqualTo(7L);
        verify(messageRepository).countUnread(conversationId, userId, 50L);
    }
}
