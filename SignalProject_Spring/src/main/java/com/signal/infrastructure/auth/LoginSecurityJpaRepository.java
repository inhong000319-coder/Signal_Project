package com.signal.infrastructure.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface LoginSecurityJpaRepository extends JpaRepository<LoginSecurityEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LoginSecurityEntity> findByLoginId(String loginId);
}
