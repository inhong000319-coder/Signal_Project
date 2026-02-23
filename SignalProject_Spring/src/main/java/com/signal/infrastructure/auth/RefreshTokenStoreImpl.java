package com.signal.infrastructure.auth;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.signal.application.auth.port.RefreshTokenStore;

@Repository
public class RefreshTokenStoreImpl implements RefreshTokenStore {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    public RefreshTokenStoreImpl(RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    @Override
    public void store(Long userId, String tokenId, Instant expiresAt, Instant now) {
        refreshTokenJpaRepository.save(new RefreshTokenEntity(tokenId, userId, expiresAt, now));
    }

    @Override
    public void revoke(String tokenId, Instant now) {
        refreshTokenJpaRepository.revoke(tokenId, now);
    }

    @Override
    public boolean isActive(Long userId, String tokenId, Instant now) {
        return refreshTokenJpaRepository.existsByTokenIdAndUserIdAndRevokedAtIsNullAndExpiresAtAfter(tokenId, userId, now);
    }

    @Override
    public void revokeAllForUser(Long userId, Instant now) {
        refreshTokenJpaRepository.revokeAllForUser(userId, now);
    }
}
