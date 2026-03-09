package com.signal.entry.auth.dto;

public record MeResponse(Long userId, String loginId, String nickname, String userCode) {
}
