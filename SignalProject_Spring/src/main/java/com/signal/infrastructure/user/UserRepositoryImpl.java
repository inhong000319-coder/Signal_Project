package com.signal.infrastructure.user;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.signal.domain.user.User;
import com.signal.domain.user.port.UserRepository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity saved = userJpaRepository.save(
            new UserEntity(
                user.getLoginId(),
                user.getPasswordHash(),
                user.getNickname(),
                user.getUserCode(),
                user.getCreatedAt()
            )
        );
        return User.restore(
            saved.getUserId(),
            saved.getLoginId(),
            saved.getPasswordHash(),
            saved.getNickname(),
            saved.getUserCode(),
            saved.getCreatedAt()
        );
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return userJpaRepository.findByLoginId(loginId)
            .map(e -> User.restore(
                e.getUserId(),
                e.getLoginId(),
                e.getPasswordHash(),
                e.getNickname(),
                e.getUserCode(),
                e.getCreatedAt()
            ));
    }

    @Override
    public Optional<User> findByUserCode(String userCode) {
        return userJpaRepository.findByUserCode(userCode)
            .map(e -> User.restore(
                e.getUserId(),
                e.getLoginId(),
                e.getPasswordHash(),
                e.getNickname(),
                e.getUserCode(),
                e.getCreatedAt()
            ));
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId)
            .map(e -> User.restore(
                e.getUserId(),
                e.getLoginId(),
                e.getPasswordHash(),
                e.getNickname(),
                e.getUserCode(),
                e.getCreatedAt()
            ));
    }

    @Override
    public void updatePasswordHash(Long userId, String passwordHash) {
        userJpaRepository.updatePasswordHash(userId, passwordHash);
    }
}
