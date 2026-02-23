package com.signal.entry.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank
    @Size(min = 4, max = 100)
    String loginId,
    @NotBlank
    @Size(min = 8, max = 64)
    String password
) {
}
