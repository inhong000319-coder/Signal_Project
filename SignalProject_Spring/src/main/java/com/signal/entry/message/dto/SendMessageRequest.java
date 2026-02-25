package com.signal.entry.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SendMessageRequest(
    @NotNull
    Long conversationId,
    @NotBlank
    @Size(max = 2000)
    String content,
    @NotBlank
    @Size(max = 100)
    String clientMessageKey
) {
}
