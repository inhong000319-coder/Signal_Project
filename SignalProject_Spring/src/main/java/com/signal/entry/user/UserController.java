package com.signal.entry.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.signal.application.user.ChangePasswordCommand;
import com.signal.application.user.ChangePasswordUseCase;
import com.signal.application.user.SignUpCommand;
import com.signal.application.user.SignUpResult;
import com.signal.application.user.UserSignUpUseCase;
import com.signal.entry.user.dto.ChangePasswordRequest;
import com.signal.entry.user.dto.SignUpRequest;
import com.signal.entry.user.dto.SignUpResponse;
import com.signal.infrastructure.security.UserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserSignUpUseCase userSignUpUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;

    public UserController(UserSignUpUseCase userSignUpUseCase, ChangePasswordUseCase changePasswordUseCase) {
        this.userSignUpUseCase = userSignUpUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
    }

    @PostMapping
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        SignUpResult result = userSignUpUseCase.signUp(
            new SignUpCommand(request.loginId(), request.password(), request.nickname())
        );
        return ResponseEntity.ok(new SignUpResponse(result.getUserId(), result.getUserCode()));
    }

    @PostMapping("/me/password")
    public ResponseEntity<Void> changePassword(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody ChangePasswordRequest request
    ) {
        changePasswordUseCase.changePassword(
            new ChangePasswordCommand(
                principal == null ? null : principal.getUserId(),
                request.currentPassword(),
                request.newPassword()
            )
        );
        return ResponseEntity.ok().build();
    }
}
