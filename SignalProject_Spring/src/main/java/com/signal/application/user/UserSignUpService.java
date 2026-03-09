package com.signal.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;

import com.signal.common.ClockHolder;
import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;
import com.signal.domain.user.User;
import com.signal.domain.user.port.UserRepository;
import com.signal.application.user.port.PasswordHasher;
import com.signal.application.user.port.UserCodeGenerator;

@Service
public class UserSignUpService implements UserSignUpUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserCodeGenerator userCodeGenerator;
    private final ClockHolder clockHolder;

    public UserSignUpService(
        UserRepository userRepository,
        PasswordHasher passwordHasher,
        UserCodeGenerator userCodeGenerator,
        ClockHolder clockHolder
    ) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.userCodeGenerator = userCodeGenerator;
        this.clockHolder = clockHolder;
    }

    @Override
    @Transactional
    public SignUpResult signUp(SignUpCommand command) {
        validate(command);
        enforceLoginIdUnique(command.getLoginId());
        String userCode = generateUniqueUserCode();
        String passwordHash = passwordHasher.hash(command.getRawPassword());

        User created = User.createNew(
            command.getLoginId(),
            passwordHash,
            command.getNickname(),
            userCode,
            clockHolder.now()
        );

        try {
            User saved = userRepository.save(created);
            return new SignUpResult(saved.getUserId(), saved.getUserCode());
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID, "duplicate user");
        }
    }

    private void validate(SignUpCommand command) {
        if (command == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "signup command required");
        }
        if (!StringUtils.hasText(command.getLoginId())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "loginId required");
        }
        if (!StringUtils.hasText(command.getRawPassword())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "password required");
        }
        if (!StringUtils.hasText(command.getNickname())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "nickname required");
        }
    }

    private void enforceLoginIdUnique(String loginId) {
        if (userRepository.findByLoginId(loginId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID, "loginId already exists");
        }
    }

    private String generateUniqueUserCode() {
        for (int i = 0; i < 5; i++) {
            String candidate = userCodeGenerator.generate();
            if (userRepository.findByUserCode(candidate).isEmpty()) {
                return candidate;
            }
        }
        throw new BusinessException(ErrorCode.DUPLICATE_USER_CODE, "userCode generation failed");
    }
}
