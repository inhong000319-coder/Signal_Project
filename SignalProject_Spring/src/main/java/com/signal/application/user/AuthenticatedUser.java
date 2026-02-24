package com.signal.application.user;

public final class AuthenticatedUser {
    private final Long userId;
    private final String loginId;
    private final String nickname;
    private final String userCode;

    public AuthenticatedUser(Long userId, String loginId, String nickname, String userCode) {
        this.userId = userId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.userCode = userCode;
    }

    public Long getUserId() {
        return userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserCode() {
        return userCode;
    }
}
