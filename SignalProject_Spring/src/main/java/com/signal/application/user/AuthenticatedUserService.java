package com.signal.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.user.User;
import com.signal.domain.user.port.UserRepository;

@Service
public class AuthenticatedUserService implements AuthenticatedUserUseCase {
    private final UserRepository userRepository;

    public AuthenticatedUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUser getAuthenticatedUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "userId required");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "user not found"));
        return new AuthenticatedUser(
            user.getUserId(),
            user.getLoginId(),
            user.getNickname(),
            user.getUserCode()
        );
    }
}
