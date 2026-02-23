package com.signal.entry.common;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.signal.common.exception.BusinessException;
import com.signal.common.exception.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex) {
        HttpStatus status = mapStatus(ex.getErrorCode());
        return ResponseEntity.status(status)
            .body(new ApiErrorResponse(ex.getErrorCode().name(), ex.getMessage(), List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new ErrorDetail(err.getField(), err.getDefaultMessage()))
            .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ApiErrorResponse(ErrorCode.INVALID_INPUT.name(), "invalid input", details));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ApiErrorResponse(ErrorCode.DUPLICATE_LOGIN_ID.name(), "duplicate", List.of()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ApiErrorResponse(ErrorCode.UNAUTHORIZED.name(), "unauthorized", List.of()));
    }

    private HttpStatus mapStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case DUPLICATE_LOGIN_ID, DUPLICATE_USER_CODE, FRIENDSHIP_ALREADY_EXISTS, DUPLICATE_MESSAGE_KEY -> HttpStatus.CONFLICT;
            case INVALID_INPUT, SELF_FRIENDSHIP -> HttpStatus.BAD_REQUEST;
            case INVALID_CREDENTIALS, UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case ACCOUNT_LOCKED -> HttpStatus.TOO_MANY_REQUESTS;
            case USER_NOT_FOUND, FRIENDSHIP_NOT_FOUND, CONVERSATION_NOT_FOUND, MESSAGE_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case FRIENDSHIP_INVALID_STATE, FRIENDSHIP_NOT_ACCEPTED, CONVERSATION_INACTIVE -> HttpStatus.CONFLICT;
            case FORBIDDEN, NOT_MEMBER -> HttpStatus.FORBIDDEN;
            case SYNC_CURSOR_CONFLICT -> HttpStatus.CONFLICT;
        };
    }
}
