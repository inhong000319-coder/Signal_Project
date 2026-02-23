package com.signal.application.auth;

public interface RefreshUseCase {
    AuthTokenPair refresh(RefreshCommand command);
}
