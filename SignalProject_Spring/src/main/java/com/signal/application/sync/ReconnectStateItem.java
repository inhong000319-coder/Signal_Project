package com.signal.application.sync;

public final class ReconnectStateItem {
    private final Long messageId;
    private final Long userId;
    private final String state;

    public ReconnectStateItem(Long messageId, Long userId, String state) {
        this.messageId = messageId;
        this.userId = userId;
        this.state = state;
    }

    public Long getMessageId() {
        return messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getState() {
        return state;
    }
}
