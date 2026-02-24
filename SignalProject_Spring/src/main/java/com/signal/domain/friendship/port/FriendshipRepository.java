package com.signal.domain.friendship.port;

import java.util.List;
import java.util.Optional;

import com.signal.domain.friendship.Friendship;

public interface FriendshipRepository {
    Optional<Friendship> findByPair(Long requesterUserId, Long targetUserId);
    Optional<Friendship> findByEither(Long userIdA, Long userIdB);
    Friendship save(Friendship friendship);
    boolean updateStatus(Long requesterUserId, Long targetUserId, com.signal.domain.friendship.FriendshipStatus status, java.time.Instant acceptedAt);
    List<Friendship> findAcceptedByUser(Long userId);
}
