package com.signal.application.auth;

public final class LogoutCommand {
    private final Long userId;
    private final String refreshToken;

    public LogoutCommand(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
