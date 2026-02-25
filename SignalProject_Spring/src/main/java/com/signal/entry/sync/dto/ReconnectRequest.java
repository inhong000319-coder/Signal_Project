package com.signal.entry.sync.dto;

import jakarta.validation.constraints.NotNull;

public record ReconnectRequest(
    @NotNull
    Long conversationId,
    Long clientLastDeliveredMessageId,
    Long clientLastReadMessageId,
    Integer limit
) {
}
