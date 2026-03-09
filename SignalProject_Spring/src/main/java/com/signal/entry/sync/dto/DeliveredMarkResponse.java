package com.signal.entry.sync.dto;

public record DeliveredMarkResponse(
    Long conversationId,
    Long userId,
    Long lastDeliveredMessageId
) {
}
