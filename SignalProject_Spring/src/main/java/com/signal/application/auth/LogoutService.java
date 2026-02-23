package com.signal.application.auth;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.signal.application.auth.port.RefreshTokenStore;
import com.signal.application.auth.port.TokenParser;
import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;

@Service
public class LogoutService implements LogoutUseCase {
    private final TokenParser tokenParser;
    private final RefreshTokenStore refreshTokenStore;
    private final ClockHolder clockHolder;

    public LogoutService(
        TokenParser tokenParser,
        RefreshTokenStore refreshTokenStore,
        ClockHolder clockHolder
    ) {
        this.tokenParser = tokenParser;
        this.refreshTokenStore = refreshTokenStore;
        this.clockHolder = clockHolder;
    }

    @Override
    @Transactional
    public void logout(LogoutCommand command) {
        validate(command);
        Instant now = clockHolder.now();
        Long tokenUserId = tokenParser.parseUserId(command.getRefreshToken());
        if (!command.getUserId().equals(tokenUserId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "token user mismatch");
        }
        String tokenId = tokenParser.parseTokenId(command.getRefreshToken());
        refreshTokenStore.revoke(tokenId, now);
    }

    private void validate(LogoutCommand command) {
        if (command == null || command.getUserId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        if (!StringUtils.hasText(command.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "refresh token required");
        }
    }
}
