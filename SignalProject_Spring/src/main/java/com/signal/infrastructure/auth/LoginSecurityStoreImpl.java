package com.signal.infrastructure.auth;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.signal.application.auth.port.LoginSecurityRecord;
import com.signal.application.auth.port.LoginSecurityStore;

@Repository
public class LoginSecurityStoreImpl implements LoginSecurityStore {
    private final LoginSecurityJpaRepository loginSecurityJpaRepository;

    public LoginSecurityStoreImpl(LoginSecurityJpaRepository loginSecurityJpaRepository) {
        this.loginSecurityJpaRepository = loginSecurityJpaRepository;
    }

    @Override
    public LoginSecurityRecord getOrCreateForUpdate(String loginId, Instant now) {
        LoginSecurityEntity entity = loginSecurityJpaRepository.findByLoginId(loginId)
            .orElseGet(() -> loginSecurityJpaRepository.save(new LoginSecurityEntity(loginId, 0, null, now)));
        return new LoginSecurityRecord(entity.getLoginId(), entity.getFailedCount(), entity.getLockedUntil());
    }

    @Override
    public void recordFailure(String loginId, int failedCount, Instant lockedUntil, Instant now) {
        LoginSecurityEntity entity = loginSecurityJpaRepository.findById(loginId)
            .orElseGet(() -> new LoginSecurityEntity(loginId, 0, null, now));
        entity.update(failedCount, lockedUntil, now);
        loginSecurityJpaRepository.save(entity);
    }

    @Override
    public void recordSuccess(String loginId, Instant now) {
        LoginSecurityEntity entity = loginSecurityJpaRepository.findById(loginId)
            .orElseGet(() -> new LoginSecurityEntity(loginId, 0, null, now));
        entity.update(0, null, now);
        loginSecurityJpaRepository.save(entity);
    }
}
