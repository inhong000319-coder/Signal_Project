package com.signal.application.auth.port;

import java.time.Instant;

public interface LoginAuditStore {
    void record(String loginId, Long userId, boolean success, String reason, String ip, String userAgent, Instant occurredAt);
}
