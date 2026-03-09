package com.signal.application.auth;

public interface LoginUseCase {
    AuthTokenPair login(LoginCommand command);
}
