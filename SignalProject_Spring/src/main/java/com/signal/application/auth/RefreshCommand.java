package com.signal.application.auth;

public final class RefreshCommand {
    private final String refreshToken;

    public RefreshCommand(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
