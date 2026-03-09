package com.signal.application.user;

public interface AuthenticatedUserUseCase {
    AuthenticatedUser getAuthenticatedUser(Long userId);
}
