package com.signal.entry.conversation.dto;

import java.time.Instant;

public record ConversationSummaryResponse(
    Long conversationId,
    String type,
    boolean active,
    Long lastMessageId,
    String lastMessageContent,
    Long lastMessageSenderUserId,
    Instant lastMessageCreatedAt,
    long unreadCount
) {
}
