package com.signal.application.auth.port;

public interface LoginSecurityPolicy {
    int maxFailures();
    int lockMinutes();
}
