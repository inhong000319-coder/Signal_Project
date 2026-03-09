package com.signal.infrastructure.auth;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, String> {
    boolean existsByTokenIdAndUserIdAndRevokedAtIsNullAndExpiresAtAfter(String tokenId, Long userId, Instant now);

    @Modifying
    @Query("update RefreshTokenEntity r set r.revokedAt = :now where r.tokenId = :tokenId and r.revokedAt is null")
    int revoke(@Param("tokenId") String tokenId, @Param("now") Instant now);

    @Modifying
    @Query("update RefreshTokenEntity r set r.revokedAt = :now where r.userId = :userId and r.revokedAt is null")
    int revokeAllForUser(@Param("userId") Long userId, @Param("now") Instant now);
}
