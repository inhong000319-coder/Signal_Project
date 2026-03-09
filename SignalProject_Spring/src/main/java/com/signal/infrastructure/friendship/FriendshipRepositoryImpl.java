package com.signal.infrastructure.friendship;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.signal.domain.friendship.Friendship;
import com.signal.domain.friendship.FriendshipStatus;
import com.signal.domain.friendship.port.FriendshipRepository;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepository {
    private final FriendshipJpaRepository friendshipJpaRepository;

    public FriendshipRepositoryImpl(FriendshipJpaRepository friendshipJpaRepository) {
        this.friendshipJpaRepository = friendshipJpaRepository;
    }

    @Override
    public Optional<Friendship> findByPair(Long requesterUserId, Long targetUserId) {
        return friendshipJpaRepository.findById(new FriendshipId(requesterUserId, targetUserId))
            .map(this::toDomain);
    }

    @Override
    public Optional<Friendship> findByEither(Long userIdA, Long userIdB) {
        return friendshipJpaRepository.findForUpdateByEither(userIdA, userIdB)
            .map(this::toDomain);
    }

    @Override
    public Friendship save(Friendship friendship) {
        FriendshipEntity saved = friendshipJpaRepository.save(
            new FriendshipEntity(
                new FriendshipId(friendship.getRequesterUserId(), friendship.getTargetUserId()),
                friendship.getStatus(),
                friendship.getRequestedAt(),
                friendship.getAcceptedAt()
            )
        );
        return toDomain(saved);
    }

    @Override
    public boolean updateStatus(Long requesterUserId, Long targetUserId, FriendshipStatus status, Instant acceptedAt) {
        return friendshipJpaRepository.findById(new FriendshipId(requesterUserId, targetUserId))
            .map(entity -> {
                entity.updateStatus(status, acceptedAt);
                return true;
            })
            .orElse(false);
    }

    @Override
    public List<Friendship> findAcceptedByUser(Long userId) {
        return friendshipJpaRepository.findByUserAndStatus(userId, FriendshipStatus.ACCEPTED).stream()
            .map(this::toDomain)
            .toList();
    }

    private Friendship toDomain(FriendshipEntity entity) {
        return Friendship.restore(
            entity.getId().getRequesterUserId(),
            entity.getId().getTargetUserId(),
            entity.getStatus(),
            entity.getRequestedAt(),
            entity.getAcceptedAt()
        );
    }
}
