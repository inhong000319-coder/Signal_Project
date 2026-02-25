package com.signal.entry.sync.dto;

public record DeliveredBatchMarkResponse(
    Long conversationId,
    Long userId,
    Long lastDeliveredMessageId
) {
}
