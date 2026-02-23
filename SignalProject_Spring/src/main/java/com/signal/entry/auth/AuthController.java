package com.signal.entry.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.signal.application.auth.AuthTokenPair;
import com.signal.application.auth.LoginCommand;
import com.signal.application.auth.LoginUseCase;
import com.signal.application.auth.LogoutCommand;
import com.signal.application.auth.LogoutUseCase;
import com.signal.application.auth.RefreshCommand;
import com.signal.application.auth.RefreshUseCase;
import com.signal.application.user.AuthenticatedUser;
import com.signal.application.user.AuthenticatedUserUseCase;
import com.signal.entry.auth.dto.LoginRequest;
import com.signal.entry.auth.dto.LoginResponse;
import com.signal.entry.auth.dto.LogoutRequest;
import com.signal.entry.auth.dto.MeResponse;
import com.signal.entry.auth.dto.RefreshRequest;
import com.signal.entry.auth.dto.RefreshResponse;
import com.signal.infrastructure.security.UserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;
    private final LogoutUseCase logoutUseCase;
    private final AuthenticatedUserUseCase authenticatedUserUseCase;

    public AuthController(
        LoginUseCase loginUseCase,
        RefreshUseCase refreshUseCase,
        LogoutUseCase logoutUseCase,
        AuthenticatedUserUseCase authenticatedUserUseCase
    ) {
        this.loginUseCase = loginUseCase;
        this.refreshUseCase = refreshUseCase;
        this.logoutUseCase = logoutUseCase;
        this.authenticatedUserUseCase = authenticatedUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest http) {
        String ip = http.getRemoteAddr();
        String userAgent = http.getHeader("User-Agent");
        AuthTokenPair result = loginUseCase.login(new LoginCommand(request.loginId(), request.password(), ip, userAgent));
        return ResponseEntity.ok(new LoginResponse(
            result.getAccessToken(),
            result.getRefreshToken(),
            result.getAccessTokenExpiresAt(),
            result.getRefreshTokenExpiresAt()
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        AuthTokenPair result = refreshUseCase.refresh(new RefreshCommand(request.refreshToken()));
        return ResponseEntity.ok(new RefreshResponse(
            result.getAccessToken(),
            result.getRefreshToken(),
            result.getAccessTokenExpiresAt(),
            result.getRefreshTokenExpiresAt()
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody LogoutRequest request
    ) {
        logoutUseCase.logout(new LogoutCommand(
            principal == null ? null : principal.getUserId(),
            request.refreshToken()
        ));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        AuthenticatedUser user = authenticatedUserUseCase.getAuthenticatedUser(principal == null ? null : principal.getUserId());
        return ResponseEntity.ok(new MeResponse(
            user.getUserId(),
            user.getLoginId(),
            user.getNickname(),
            user.getUserCode()
        ));
    }
}
