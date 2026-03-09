package com.signal.domain.user.port;

import java.util.Optional;

import com.signal.domain.user.User;

public interface UserRepository {
    User save(User user);
    Optional<User> findByLoginId(String loginId);
    Optional<User> findByUserCode(String userCode);
    Optional<User> findById(Long userId);
    void updatePasswordHash(Long userId, String passwordHash);
}
