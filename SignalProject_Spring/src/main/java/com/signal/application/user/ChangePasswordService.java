package com.signal.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.signal.application.auth.port.RefreshTokenStore;
import com.signal.application.user.port.PasswordHasher;
import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.user.User;
import com.signal.domain.user.port.UserRepository;

@Service
public class ChangePasswordService implements ChangePasswordUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenStore refreshTokenStore;
    private final ClockHolder clockHolder;

    public ChangePasswordService(
        UserRepository userRepository,
        PasswordHasher passwordHasher,
        RefreshTokenStore refreshTokenStore,
        ClockHolder clockHolder
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.refreshTokenStore = refreshTokenStore;
        this.clockHolder = clockHolder;
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        validate(command);
        User user = userRepository.findById(command.getUserId())
            .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "user not found"));
        if (!passwordHasher.matches(command.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS, "invalid credentials");
        }
        String newHash = passwordHasher.hash(command.getNewPassword());
        userRepository.updatePasswordHash(user.getUserId(), newHash);
        refreshTokenStore.revokeAllForUser(user.getUserId(), clockHolder.now());
    }

    private void validate(ChangePasswordCommand command) {
        if (command == null || command.getUserId() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "userId required");
        }
        if (!StringUtils.hasText(command.getCurrentPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "currentPassword required");
        }
        if (!StringUtils.hasText(command.getNewPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "newPassword required");
        }
        if (command.getNewPassword().length() < 8 || command.getNewPassword().length() > 64) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "newPassword length invalid");
        }
    }
}
