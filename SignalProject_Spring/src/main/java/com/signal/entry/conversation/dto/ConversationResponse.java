package com.signal.entry.conversation.dto;

import java.time.Instant;

public record ConversationResponse(
    Long conversationId,
    String type,
    boolean active,
    Instant createdAt
) {
}
