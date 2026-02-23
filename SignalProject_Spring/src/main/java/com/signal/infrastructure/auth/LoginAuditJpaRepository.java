package com.signal.infrastructure.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAuditJpaRepository extends JpaRepository<LoginAuditEntity, Long> {
}
