package com.signal.application.auth.port;

import java.time.Instant;

public interface RefreshTokenStore {
    void store(Long userId, String tokenId, Instant expiresAt, Instant now);
    void revoke(String tokenId, Instant now);
    boolean isActive(Long userId, String tokenId, Instant now);
    void revokeAllForUser(Long userId, Instant now);
}
