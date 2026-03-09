package com.signal.entry.message.dto;

import java.time.Instant;

public record MessageListItemResponse(
    Long messageId,
    Long conversationId,
    Long senderUserId,
    String content,
    String clientMessageKey,
    Instant createdAt
) {
}
