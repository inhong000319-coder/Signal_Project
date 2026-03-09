package com.signal.application.auth.port;

public interface TokenIssuer {
    IssuedToken createAccessToken(Long userId, java.time.Instant now);
    IssuedToken createRefreshToken(Long userId, String tokenId, java.time.Instant now);
}
