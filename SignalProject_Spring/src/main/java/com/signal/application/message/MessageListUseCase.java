package com.signal.application.message;

public interface MessageListUseCase {
    MessageListResult list(MessageListQuery query);
}
