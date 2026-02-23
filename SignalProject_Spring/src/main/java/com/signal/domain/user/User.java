package com.signal.domain.user;

import java.time.Instant;
import java.util.Objects;

public final class User {
    private final Long userId;
    private final String loginId;
    private final String passwordHash;
    private final String nickname;
    private final String userCode;
    private final Instant createdAt;

    private User(Long userId, String loginId, String passwordHash, String nickname, String userCode, Instant createdAt) {
        this.userId = userId;
        this.loginId = Objects.requireNonNull(loginId, "loginId");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
        this.nickname = Objects.requireNonNull(nickname, "nickname");
        this.userCode = Objects.requireNonNull(userCode, "userCode");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public static User createNew(String loginId, String passwordHash, String nickname, String userCode, Instant createdAt) {
        return new User(null, loginId, passwordHash, nickname, userCode, createdAt);
    }

    public static User restore(Long userId, String loginId, String passwordHash, String nickname, String userCode, Instant createdAt) {
        return new User(userId, loginId, passwordHash, nickname, userCode, createdAt);
    }

    public Long getUserId() {
        return userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserCode() {
        return userCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
