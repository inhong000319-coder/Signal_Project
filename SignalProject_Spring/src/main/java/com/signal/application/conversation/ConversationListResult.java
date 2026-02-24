package com.signal.application.conversation;

import java.util.List;

public final class ConversationListResult {
    private final List<ConversationSummaryItem> conversations;

    public ConversationListResult(List<ConversationSummaryItem> conversations) {
        this.conversations = conversations;
    }

    public List<ConversationSummaryItem> getConversations() {
        return conversations;
    }
}
