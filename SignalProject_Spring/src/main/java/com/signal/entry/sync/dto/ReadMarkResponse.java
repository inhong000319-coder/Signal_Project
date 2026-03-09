package com.signal.entry.sync.dto;

public record ReadMarkResponse(
    Long conversationId,
    Long userId,
    Long lastReadMessageId
) {
}
