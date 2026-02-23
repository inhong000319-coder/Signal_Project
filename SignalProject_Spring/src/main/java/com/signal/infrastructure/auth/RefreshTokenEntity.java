package com.signal.infrastructure.auth;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    @Column(name = "token_id", nullable = false, length = 36)
    private String tokenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RefreshTokenEntity() {
    }

    public RefreshTokenEntity(String tokenId, Long userId, Instant expiresAt, Instant createdAt) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public String getTokenId() {
        return tokenId;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
