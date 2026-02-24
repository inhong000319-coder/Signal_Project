package com.signal.entry.conversation.dto;

import java.util.List;

public record ConversationListResponse(
    List<ConversationSummaryResponse> conversations
) {
}
