package com.signal.application.conversation;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.ConversationMember;
import com.signal.domain.conversation.ConversationMemberRole;
import com.signal.domain.conversation.ConversationType;
import com.signal.domain.conversation.port.ConversationMemberRepository;
import com.signal.domain.conversation.port.ConversationRepository;
import com.signal.domain.friendship.Friendship;
import com.signal.domain.friendship.FriendshipStatus;
import com.signal.domain.friendship.port.FriendshipRepository;
import com.signal.domain.user.port.UserRepository;

@Service
public class ConversationService implements ConversationUseCase {
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository conversationMemberRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final ClockHolder clockHolder;

    public ConversationService(
        ConversationRepository conversationRepository,
        ConversationMemberRepository conversationMemberRepository,
        FriendshipRepository friendshipRepository,
        UserRepository userRepository,
        ClockHolder clockHolder
    ) {
        this.conversationRepository = conversationRepository;
        this.conversationMemberRepository = conversationMemberRepository;
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.clockHolder = clockHolder;
    }

    @Override
    @Transactional
    public ConversationResult createDirect(CreateDirectConversationCommand command) {
        validate(command);
        ensureUserExists(command.getTargetUserId());

        Friendship friendship = friendshipRepository.findByEither(command.getRequesterUserId(), command.getTargetUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDSHIP_NOT_FOUND, "friendship not found"));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.FRIENDSHIP_NOT_ACCEPTED, "friendship not accepted");
        }

        Conversation existing = conversationRepository.findDirectBetween(command.getRequesterUserId(), command.getTargetUserId())
            .orElse(null);
        if (existing != null) {
            return toResult(existing);
        }

        Instant now = clockHolder.now();
        Conversation created = conversationRepository.save(Conversation.createNew(ConversationType.DIRECT, now));

        conversationMemberRepository.saveAll(List.of(
            ConversationMember.create(created.getConversationId(), command.getRequesterUserId(), ConversationMemberRole.OWNER),
            ConversationMember.create(created.getConversationId(), command.getTargetUserId(), ConversationMemberRole.MEMBER)
        ));

        return toResult(created);
    }

    private void validate(CreateDirectConversationCommand command) {
        if (command == null || command.getRequesterUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (command.getTargetUserId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "targetUserId required");
        }
        if (command.getRequesterUserId().equals(command.getTargetUserId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "self conversation not allowed");
        }
    }

    private void ensureUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "user not found");
        }
    }

    private ConversationResult toResult(Conversation conversation) {
        return new ConversationResult(
            conversation.getConversationId(),
            conversation.getType().name(),
            conversation.isActive(),
            conversation.getCreatedAt()
        );
    }
}
