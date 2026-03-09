package com.signal.entry.sync.dto;

import jakarta.validation.constraints.NotNull;

public record DeliveredMarkRequest(
    @NotNull
    Long conversationId,
    @NotNull
    Long messageId
) {
}
