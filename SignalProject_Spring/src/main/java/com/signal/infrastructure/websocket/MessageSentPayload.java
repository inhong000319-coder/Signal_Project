package com.signal.infrastructure.websocket;

import java.time.Instant;

public record MessageSentPayload(
    Long messageId,
    Long conversationId,
    Long senderUserId,
    String content,
    String clientMessageKey,
    Instant createdAt
) {
}
