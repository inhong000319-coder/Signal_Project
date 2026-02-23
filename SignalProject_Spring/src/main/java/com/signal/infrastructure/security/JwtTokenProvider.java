package com.signal.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.signal.application.auth.port.IssuedToken;
import com.signal.application.auth.port.TokenIssuer;
import com.signal.application.auth.port.TokenParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider implements TokenIssuer, TokenParser {
    private final JwtProperties properties;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
    }

    @Override
    public IssuedToken createAccessToken(Long userId, Instant now) {
        Instant exp = now.plusSeconds(properties.getAccessTokenExpMinutes() * 60L);
        String token = Jwts.builder()
            .issuer(properties.getIssuer())
            .subject(String.valueOf(userId))
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8)))
            .compact();
        return new IssuedToken(token, exp);
    }

    @Override
    public IssuedToken createRefreshToken(Long userId, String tokenId, Instant now) {
        Instant exp = now.plusSeconds(properties.getRefreshTokenExpDays() * 24L * 60L * 60L);
        String token = Jwts.builder()
            .issuer(properties.getIssuer())
            .subject(String.valueOf(userId))
            .id(tokenId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8)))
            .compact();
        return new IssuedToken(token, exp);
    }

    public Long parseUserId(String token) {
        Claims claims = parse(token);
        return Long.valueOf(claims.getSubject());
    }

    public String parseTokenId(String token) {
        Claims claims = parse(token);
        return claims.getId();
    }

    private Claims parse(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8)))
            .requireIssuer(properties.getIssuer())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
