package com.signal.infrastructure.auth;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.signal.application.auth.port.LoginAuditStore;

@Repository
public class LoginAuditStoreImpl implements LoginAuditStore {
    private final LoginAuditJpaRepository loginAuditJpaRepository;

    public LoginAuditStoreImpl(LoginAuditJpaRepository loginAuditJpaRepository) {
        this.loginAuditJpaRepository = loginAuditJpaRepository;
    }

    @Override
    public void record(String loginId, Long userId, boolean success, String reason, String ip, String userAgent, Instant occurredAt) {
        loginAuditJpaRepository.save(new LoginAuditEntity(loginId, userId, success, reason, ip, userAgent, occurredAt));
    }
}
