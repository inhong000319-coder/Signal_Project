package com.signal.application.friendship;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.port.ConversationRepository;
import com.signal.domain.friendship.Friendship;
import com.signal.domain.friendship.port.FriendshipRepository;
import com.signal.domain.user.User;
import com.signal.domain.user.port.UserRepository;

@Service
public class FriendListService implements FriendListUseCase {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;

    public FriendListService(
        FriendshipRepository friendshipRepository,
        UserRepository userRepository,
        ConversationRepository conversationRepository
    ) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public FriendListResult list(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }

        List<Friendship> friendships = friendshipRepository.findAcceptedByUser(userId);
        List<FriendSummary> results = new ArrayList<>();

        for (Friendship friendship : friendships) {
            Long friendUserId = friendship.getRequesterUserId().equals(userId)
                ? friendship.getTargetUserId()
                : friendship.getRequesterUserId();

            User friend = userRepository.findById(friendUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "user not found"));

            Conversation conversation = conversationRepository.findDirectBetween(userId, friendUserId).orElse(null);
            Long conversationId = conversation == null ? null : conversation.getConversationId();

            results.add(new FriendSummary(
                friend.getUserId(),
                friend.getNickname(),
                friend.getUserCode(),
                conversationId
            ));
        }

        return new FriendListResult(results);
    }
}
