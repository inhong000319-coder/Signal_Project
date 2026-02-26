package com.signal.application.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.signal.common.ClockHolder;
import com.signal.domain.conversation.port.ConversationMemberRepository;
import com.signal.domain.message.Message;
import com.signal.domain.message.port.MessageRepository;
import com.signal.domain.message.port.MessageStateRepository;
import com.signal.domain.sync.port.SyncCursorRepository;

@ExtendWith(MockitoExtension.class)
class ReadBatchMarkServiceTest {
    @Mock
    ConversationMemberRepository conversationMemberRepository;

    @Mock
    MessageRepository messageRepository;

    @Mock
    MessageStateRepository messageStateRepository;

    @Mock
    SyncCursorRepository syncCursorRepository;

    @Mock
    ClockHolder clockHolder;

    @Mock
    org.springframework.context.ApplicationEventPublisher eventPublisher;

    @InjectMocks
    ReadBatchMarkService service;

    @Test
    void deduplicatesMessageIdsAndUpdatesCursorWithMax() {
        Long userId = 1L;
        Long conversationId = 10L;

        when(conversationMemberRepository.exists(conversationId, userId)).thenReturn(true);
        when(clockHolder.now()).thenReturn(Instant.parse("2026-02-19T12:00:00Z"));

        when(messageRepository.findByIds(List.of(2L, 3L))).thenReturn(List.of(
            Message.restore(2L, conversationId, 9L, "a", "k1", Instant.now()),
            Message.restore(3L, conversationId, 9L, "b", "k2", Instant.now())
        ));

        when(syncCursorRepository.updateLastRead(conversationId, userId, 3L)).thenReturn(true);

        ReadBatchMarkResult result = service.markReadBatch(
            new ReadBatchMarkCommand(userId, conversationId, List.of(2L, 3L, 2L))
        );

        assertThat(result.getLastReadMessageId()).isEqualTo(3L);
        verify(messageRepository, times(1)).findByIds(List.of(2L, 3L));
    }
}
