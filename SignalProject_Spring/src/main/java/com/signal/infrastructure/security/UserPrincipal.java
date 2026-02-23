package com.signal.infrastructure.security;

import java.security.Principal;

public final class UserPrincipal implements Principal {
    private final Long userId;

    public UserPrincipal(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }
}
