package com.signal.infrastructure.friendship;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import com.signal.domain.friendship.FriendshipStatus;

public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, FriendshipId> {
    Optional<FriendshipEntity> findById(FriendshipId id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from FriendshipEntity f where (f.id.requesterUserId = :a and f.id.targetUserId = :b) or (f.id.requesterUserId = :b and f.id.targetUserId = :a)")
    Optional<FriendshipEntity> findForUpdateByEither(@Param("a") Long a, @Param("b") Long b);

    @Query("select f from FriendshipEntity f where f.status = :status and (f.id.requesterUserId = :userId or f.id.targetUserId = :userId)")
    List<FriendshipEntity> findByUserAndStatus(@Param("userId") Long userId, @Param("status") FriendshipStatus status);
}
