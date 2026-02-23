package com.signal.application.auth;

public final class LoginCommand {
    private final String loginId;
    private final String rawPassword;
    private final String ip;
    private final String userAgent;

    public LoginCommand(String loginId, String rawPassword, String ip, String userAgent) {
        this.loginId = loginId;
        this.rawPassword = rawPassword;
        this.ip = ip;
        this.userAgent = userAgent;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public String getIp() {
        return ip;
    }

    public String getUserAgent() {
        return userAgent;
    }
}
