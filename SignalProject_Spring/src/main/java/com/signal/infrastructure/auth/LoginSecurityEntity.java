package com.signal.infrastructure.auth;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login_security")
public class LoginSecurityEntity {
    @Id
    @Column(name = "login_id", nullable = false, length = 100)
    private String loginId;

    @Column(name = "failed_count", nullable = false)
    private int failedCount;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LoginSecurityEntity() {
    }

    public LoginSecurityEntity(String loginId, int failedCount, Instant lockedUntil, Instant updatedAt) {
        this.loginId = loginId;
        this.failedCount = failedCount;
        this.lockedUntil = lockedUntil;
        this.updatedAt = updatedAt;
    }

    public String getLoginId() {
        return loginId;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void update(int failedCount, Instant lockedUntil, Instant updatedAt) {
        this.failedCount = failedCount;
        this.lockedUntil = lockedUntil;
        this.updatedAt = updatedAt;
    }
}
