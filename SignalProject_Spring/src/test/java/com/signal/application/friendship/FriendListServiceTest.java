package com.signal.application.friendship;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.ConversationType;
import com.signal.domain.conversation.port.ConversationRepository;
import com.signal.domain.friendship.Friendship;
import com.signal.domain.friendship.FriendshipStatus;
import com.signal.domain.friendship.port.FriendshipRepository;
import com.signal.domain.user.User;
import com.signal.domain.user.port.UserRepository;

@ExtendWith(MockitoExtension.class)
class FriendListServiceTest {
    @Mock
    FriendshipRepository friendshipRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ConversationRepository conversationRepository;

    @InjectMocks
    FriendListService service;

    @Test
    void list_includesConversationIdWhenExists() {
        Long userId = 1L;
        Long friendId = 2L;

        when(friendshipRepository.findAcceptedByUser(userId)).thenReturn(List.of(
            Friendship.restore(userId, friendId, FriendshipStatus.ACCEPTED, Instant.now(), Instant.now())
        ));
        when(friendshipRepository.findByUserAndStatus(userId, FriendshipStatus.PENDING)).thenReturn(List.of());

        when(userRepository.findById(friendId)).thenReturn(java.util.Optional.of(
            User.restore(friendId, "friend", "pw", "nick", "code", Instant.now())
        ));

        when(conversationRepository.findDirectBetween(userId, friendId)).thenReturn(java.util.Optional.of(
            Conversation.restore(99L, ConversationType.DIRECT, true, Instant.now())
        ));

        FriendListResult result = service.list(userId);

        assertThat(result.getFriends()).hasSize(1);
        assertThat(result.getFriends().get(0).getConversationId()).isEqualTo(99L);
        assertThat(result.getIncomingRequests()).isEmpty();
        assertThat(result.getOutgoingRequests()).isEmpty();
    }
}
