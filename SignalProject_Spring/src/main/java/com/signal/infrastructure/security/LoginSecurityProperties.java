package com.signal.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "signal.security.login")
public class LoginSecurityProperties implements com.signal.application.auth.port.LoginSecurityPolicy {
    private int maxFailures;
    private int lockMinutes;

    @Override
    public int maxFailures() {
        return maxFailures;
    }

    public void setMaxFailures(int maxFailures) {
        this.maxFailures = maxFailures;
    }

    @Override
    public int lockMinutes() {
        return lockMinutes;
    }

    public void setLockMinutes(int lockMinutes) {
        this.lockMinutes = lockMinutes;
    }
}
