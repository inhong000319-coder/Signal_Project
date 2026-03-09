package com.signal.entry.sync.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record DeliveredBatchMarkRequest(
    @NotNull
    Long conversationId,
    @NotNull
    List<Long> messageIds
) {
}
