package com.signal.entry.sync.dto;

import java.time.Instant;

public record ReconnectMessageResponse(
    Long messageId,
    Long conversationId,
    Long senderUserId,
    String content,
    String clientMessageKey,
    Instant createdAt
) {
}
