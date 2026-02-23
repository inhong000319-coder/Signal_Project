package com.signal.infrastructure.auth;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login_audit")
public class LoginAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id", nullable = false)
    private Long auditId;

    @Column(name = "login_id", length = 100)
    private String loginId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "reason", length = 50)
    private String reason;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected LoginAuditEntity() {
    }

    public LoginAuditEntity(String loginId, Long userId, boolean success, String reason, String ip, String userAgent, Instant occurredAt) {
        this.loginId = loginId;
        this.userId = userId;
        this.success = success;
        this.reason = reason;
        this.ip = ip;
        this.userAgent = userAgent;
        this.occurredAt = occurredAt;
    }
}
