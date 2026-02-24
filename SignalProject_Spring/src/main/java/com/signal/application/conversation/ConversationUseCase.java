package com.signal.application.conversation;

public interface ConversationUseCase {
    ConversationResult createDirect(CreateDirectConversationCommand command);
}
