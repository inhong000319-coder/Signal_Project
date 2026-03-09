package com.signal.entry.sync.dto;

public record ReconnectStateResponse(
    Long messageId,
    Long userId,
    String state
) {
}
