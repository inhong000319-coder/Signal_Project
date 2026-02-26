package com.signal.infrastructure.websocket;

import java.time.Instant;

public record DeliveredUpdatePayload(
    Long conversationId,
    Long userId,
    Long messageId,
    Instant occurredAt
) {
}
