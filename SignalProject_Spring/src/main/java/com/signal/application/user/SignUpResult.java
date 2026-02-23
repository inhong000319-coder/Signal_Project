package com.signal.application.user;

public final class SignUpResult {
    private final Long userId;
    private final String userCode;

    public SignUpResult(Long userId, String userCode) {
        this.userId = userId;
        this.userCode = userCode;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserCode() {
        return userCode;
    }
}
