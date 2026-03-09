package com.signal.application.message;

public interface MessageSendUseCase {
    SendMessageResult send(SendMessageCommand command);
}
