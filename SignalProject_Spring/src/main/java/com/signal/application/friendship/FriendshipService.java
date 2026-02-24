package com.signal.application.friendship;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.friendship.Friendship;
import com.signal.domain.friendship.FriendshipStatus;
import com.signal.domain.friendship.port.FriendshipRepository;
import com.signal.domain.user.port.UserRepository;

@Service
public class FriendshipService implements FriendshipUseCase {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final ClockHolder clockHolder;

    public FriendshipService(
        FriendshipRepository friendshipRepository,
        UserRepository userRepository,
        ClockHolder clockHolder
    ) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
        this.clockHolder = clockHolder;
    }

    @Override
    @Transactional
    public FriendshipResult request(FriendRequestCommand command) {
        validateRequest(command);
        ensureUserExists(command.getTargetUserId());

        Instant now = clockHolder.now();
        Friendship existing = friendshipRepository.findByEither(command.getRequesterUserId(), command.getTargetUserId())
            .orElse(null);

        if (existing == null) {
            Friendship created = friendshipRepository.save(
                Friendship.request(command.getRequesterUserId(), command.getTargetUserId(), now)
            );
            return toResult(created);
        }

        if (existing.getStatus() == FriendshipStatus.BLOCKED) {
            throw new BusinessException(ErrorCode.FRIENDSHIP_INVALID_STATE, "friendship blocked");
        }

        if (existing.getStatus() == FriendshipStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.FRIENDSHIP_ALREADY_EXISTS, "already friends");
        }

        if (isSameDirection(existing, command.getRequesterUserId(), command.getTargetUserId())) {
            return toResult(existing);
        }

        Friendship accepted = Friendship.accept(
            existing.getRequesterUserId(),
            existing.getTargetUserId(),
            existing.getRequestedAt(),
            now
        );
        friendshipRepository.updateStatus(accepted.getRequesterUserId(), accepted.getTargetUserId(), FriendshipStatus.ACCEPTED, now);
        return toResult(accepted);
    }

    @Override
    @Transactional
    public FriendshipResult accept(FriendAcceptCommand command) {
        validateAccept(command);
        Instant now = clockHolder.now();

        Friendship existing = friendshipRepository.findByEither(command.getRequesterUserId(), command.getTargetUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDSHIP_NOT_FOUND, "friendship not found"));

        if (!isSameDirection(existing, command.getRequesterUserId(), command.getTargetUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only target can accept request");
        }

        if (existing.getStatus() != FriendshipStatus.PENDING) {
            throw new BusinessException(ErrorCode.FRIENDSHIP_INVALID_STATE, "invalid friendship state");
        }

        friendshipRepository.updateStatus(existing.getRequesterUserId(), existing.getTargetUserId(), FriendshipStatus.ACCEPTED, now);
        Friendship accepted = Friendship.accept(
            existing.getRequesterUserId(),
            existing.getTargetUserId(),
            existing.getRequestedAt(),
            now
        );
        return toResult(accepted);
    }

    @Override
    @Transactional
    public FriendshipResult block(FriendBlockCommand command) {
        validateBlock(command);
        ensureUserExists(command.getTargetUserId());
        Instant now = clockHolder.now();

        Friendship existing = friendshipRepository.findByEither(command.getBlockerUserId(), command.getTargetUserId())
            .orElse(null);

        if (existing == null) {
            Friendship created = friendshipRepository.save(
                Friendship.block(command.getBlockerUserId(), command.getTargetUserId(), now)
            );
            return toResult(created);
        }

        if (existing.getStatus() == FriendshipStatus.BLOCKED) {
            return toResult(existing);
        }

        friendshipRepository.updateStatus(existing.getRequesterUserId(), existing.getTargetUserId(), FriendshipStatus.BLOCKED, null);
        Friendship blocked = Friendship.block(existing.getRequesterUserId(), existing.getTargetUserId(), existing.getRequestedAt());
        return toResult(blocked);
    }

    private void validateRequest(FriendRequestCommand command) {
        if (command == null || command.getRequesterUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (command.getTargetUserId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "targetUserId required");
        }
        if (command.getRequesterUserId().equals(command.getTargetUserId())) {
            throw new BusinessException(ErrorCode.SELF_FRIENDSHIP, "self friendship not allowed");
        }
    }

    private void validateAccept(FriendAcceptCommand command) {
        if (command == null || command.getTargetUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (command.getRequesterUserId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "requesterUserId required");
        }
        if (command.getRequesterUserId().equals(command.getTargetUserId())) {
            throw new BusinessException(ErrorCode.SELF_FRIENDSHIP, "self friendship not allowed");
        }
    }

    private void validateBlock(FriendBlockCommand command) {
        if (command == null || command.getBlockerUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (command.getTargetUserId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "targetUserId required");
        }
        if (command.getBlockerUserId().equals(command.getTargetUserId())) {
            throw new BusinessException(ErrorCode.SELF_FRIENDSHIP, "self friendship not allowed");
        }
    }

    private void ensureUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "user not found");
        }
    }

    private boolean isSameDirection(Friendship friendship, Long requesterUserId, Long targetUserId) {
        return friendship.getRequesterUserId().equals(requesterUserId)
            && friendship.getTargetUserId().equals(targetUserId);
    }

    private FriendshipResult toResult(Friendship friendship) {
        return new FriendshipResult(
            friendship.getRequesterUserId(),
            friendship.getTargetUserId(),
            friendship.getStatus().name(),
            friendship.getRequestedAt(),
            friendship.getAcceptedAt()
        );
    }
}
