package com.signal.entry.sync.dto;

public record ReadBatchMarkResponse(
    Long conversationId,
    Long userId,
    Long lastReadMessageId
) {
}
