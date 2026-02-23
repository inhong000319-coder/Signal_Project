package com.signal.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "signal.security.jwt")
public class JwtProperties {
    private String issuer;
    private int accessTokenExpMinutes;
    private int refreshTokenExpDays;
    private String secret;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public int getAccessTokenExpMinutes() {
        return accessTokenExpMinutes;
    }

    public void setAccessTokenExpMinutes(int accessTokenExpMinutes) {
        this.accessTokenExpMinutes = accessTokenExpMinutes;
    }

    public int getRefreshTokenExpDays() {
        return refreshTokenExpDays;
    }

    public void setRefreshTokenExpDays(int refreshTokenExpDays) {
        this.refreshTokenExpDays = refreshTokenExpDays;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
