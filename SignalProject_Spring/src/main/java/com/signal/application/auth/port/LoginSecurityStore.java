package com.signal.application.auth.port;

import java.time.Instant;

public interface LoginSecurityStore {
    LoginSecurityRecord getOrCreateForUpdate(String loginId, Instant now);
    void recordFailure(String loginId, int failedCount, Instant lockedUntil, Instant now);
    void recordSuccess(String loginId, Instant now);
}
