package com.signal.application.auth;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.signal.application.auth.port.IssuedToken;
import com.signal.application.auth.port.RefreshTokenStore;
import com.signal.application.auth.port.TokenIssuer;
import com.signal.application.auth.port.TokenParser;
import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;

@Service
public class RefreshService implements RefreshUseCase {
    private final TokenParser tokenParser;
    private final TokenIssuer tokenIssuer;
    private final RefreshTokenStore refreshTokenStore;
    private final ClockHolder clockHolder;

    public RefreshService(
        TokenParser tokenParser,
        TokenIssuer tokenIssuer,
        RefreshTokenStore refreshTokenStore,
        ClockHolder clockHolder
    ) {
        this.tokenParser = tokenParser;
        this.tokenIssuer = tokenIssuer;
        this.refreshTokenStore = refreshTokenStore;
        this.clockHolder = clockHolder;
    }

    @Override
    @Transactional
    public AuthTokenPair refresh(RefreshCommand command) {
        validate(command);
        Instant now = clockHolder.now();

        Long userId = tokenParser.parseUserId(command.getRefreshToken());
        String tokenId = tokenParser.parseTokenId(command.getRefreshToken());

        if (!refreshTokenStore.isActive(userId, tokenId, now)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "refresh token inactive");
        }

        refreshTokenStore.revoke(tokenId, now);

        IssuedToken accessToken = tokenIssuer.createAccessToken(userId, now);
        String newTokenId = UUID.randomUUID().toString();
        IssuedToken refreshToken = tokenIssuer.createRefreshToken(userId, newTokenId, now);
        refreshTokenStore.store(userId, newTokenId, refreshToken.getExpiresAt(), now);

        return new AuthTokenPair(
            accessToken.getToken(),
            refreshToken.getToken(),
            accessToken.getExpiresAt(),
            refreshToken.getExpiresAt()
        );
    }

    private void validate(RefreshCommand command) {
        if (command == null || !StringUtils.hasText(command.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "refresh token required");
        }
    }
}
