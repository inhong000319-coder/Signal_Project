package com.signal.application.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.signal.application.auth.port.IssuedToken;
import com.signal.application.auth.port.LoginAuditStore;
import com.signal.application.auth.port.LoginSecurityPolicy;
import com.signal.application.auth.port.LoginSecurityRecord;
import com.signal.application.auth.port.LoginSecurityStore;
import com.signal.application.auth.port.RefreshTokenStore;
import com.signal.application.auth.port.TokenIssuer;
import com.signal.application.user.port.PasswordHasher;
import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.user.User;
import com.signal.domain.user.port.UserRepository;

@Service
public class LoginService implements LoginUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;
    private final RefreshTokenStore refreshTokenStore;
    private final LoginSecurityStore loginSecurityStore;
    private final LoginSecurityPolicy loginSecurityPolicy;
    private final LoginAuditStore loginAuditStore;
    private final ClockHolder clockHolder;

    public LoginService(
        UserRepository userRepository,
        PasswordHasher passwordHasher,
        TokenIssuer tokenIssuer,
        RefreshTokenStore refreshTokenStore,
        LoginSecurityStore loginSecurityStore,
        LoginSecurityPolicy loginSecurityPolicy,
        LoginAuditStore loginAuditStore,
        ClockHolder clockHolder
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.refreshTokenStore = refreshTokenStore;
        this.loginSecurityStore = loginSecurityStore;
        this.loginSecurityPolicy = loginSecurityPolicy;
        this.loginAuditStore = loginAuditStore;
        this.clockHolder = clockHolder;
    }

    @Override
    @Transactional
    public AuthTokenPair login(LoginCommand command) {
        validate(command);
        Instant now = clockHolder.now();

        LoginSecurityRecord record = loginSecurityStore.getOrCreateForUpdate(command.getLoginId(), now);
        if (isLocked(record, now)) {
            loginAuditStore.record(command.getLoginId(), null, false, "LOCKED", command.getIp(), command.getUserAgent(), now);
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED, "login temporarily locked");
        }

        User user = userRepository.findByLoginId(command.getLoginId())
            .orElseThrow(() -> onLoginFailure(record, command, now));
        if (!passwordHasher.matches(command.getRawPassword(), user.getPasswordHash())) {
            throw onLoginFailure(record, command, now);
        }

        loginSecurityStore.recordSuccess(command.getLoginId(), now);
        loginAuditStore.record(command.getLoginId(), user.getUserId(), true, "SUCCESS", command.getIp(), command.getUserAgent(), now);

        IssuedToken accessToken = tokenIssuer.createAccessToken(user.getUserId(), now);

        String tokenId = UUID.randomUUID().toString();
        IssuedToken refreshToken = tokenIssuer.createRefreshToken(user.getUserId(), tokenId, now);
        refreshTokenStore.store(user.getUserId(), tokenId, refreshToken.getExpiresAt(), now);

        return new AuthTokenPair(
            accessToken.getToken(),
            refreshToken.getToken(),
            accessToken.getExpiresAt(),
            refreshToken.getExpiresAt()
        );
    }

    private BusinessException onLoginFailure(LoginSecurityRecord record, LoginCommand command, Instant now) {
        int nextCount = record.getFailedCount() + 1;
        Instant lockedUntil = null;
        if (nextCount >= loginSecurityPolicy.maxFailures()) {
            lockedUntil = now.plus(loginSecurityPolicy.lockMinutes(), ChronoUnit.MINUTES);
        }
        loginSecurityStore.recordFailure(record.getLoginId(), nextCount, lockedUntil, now);
        if (lockedUntil != null) {
            loginAuditStore.record(command.getLoginId(), null, false, "LOCKED", command.getIp(), command.getUserAgent(), now);
            return new BusinessException(ErrorCode.ACCOUNT_LOCKED, "login temporarily locked");
        }
        loginAuditStore.record(command.getLoginId(), null, false, "INVALID_CREDENTIALS", command.getIp(), command.getUserAgent(), now);
        return new BusinessException(ErrorCode.INVALID_CREDENTIALS, "invalid credentials");
    }

    private boolean isLocked(LoginSecurityRecord record, Instant now) {
        return record.getLockedUntil() != null && record.getLockedUntil().isAfter(now);
    }

    private void validate(LoginCommand command) {
        if (command == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "login command required");
        }
        if (!StringUtils.hasText(command.getLoginId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "loginId required");
        }
        if (!StringUtils.hasText(command.getRawPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "password required");
        }
    }
}
