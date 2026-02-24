package com.signal.entry.conversation.dto;

import jakarta.validation.constraints.NotNull;

public record CreateDirectConversationRequest(
    @NotNull
    Long targetUserId
) {
}
