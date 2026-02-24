package com.signal.application.conversation;

public interface ConversationListUseCase {
    ConversationListResult list(ConversationListQuery query);
}
