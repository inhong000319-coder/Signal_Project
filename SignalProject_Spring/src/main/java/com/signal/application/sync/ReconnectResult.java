package com.signal.application.sync;

import java.util.List;

public final class ReconnectResult {
    private final Long conversationId;
    private final Long userId;
    private final Long effectiveLastDeliveredMessageId;
    private final Long effectiveLastReadMessageId;
    private final Long serverLastDeliveredMessageId;
    private final Long serverLastReadMessageId;
    private final List<ReconnectMessageItem> messages;
    private final List<ReconnectStateItem> states;

    public ReconnectResult(
        Long conversationId,
        Long userId,
        Long effectiveLastDeliveredMessageId,
        Long effectiveLastReadMessageId,
        Long serverLastDeliveredMessageId,
        Long serverLastReadMessageId,
        List<ReconnectMessageItem> messages,
        List<ReconnectStateItem> states
    ) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.effectiveLastDeliveredMessageId = effectiveLastDeliveredMessageId;
        this.effectiveLastReadMessageId = effectiveLastReadMessageId;
        this.serverLastDeliveredMessageId = serverLastDeliveredMessageId;
        this.serverLastReadMessageId = serverLastReadMessageId;
        this.messages = messages;
        this.states = states;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getEffectiveLastDeliveredMessageId() {
        return effectiveLastDeliveredMessageId;
    }

    public Long getEffectiveLastReadMessageId() {
        return effectiveLastReadMessageId;
    }

    public Long getServerLastDeliveredMessageId() {
        return serverLastDeliveredMessageId;
    }

    public Long getServerLastReadMessageId() {
        return serverLastReadMessageId;
    }

    public List<ReconnectMessageItem> getMessages() {
        return messages;
    }

    public List<ReconnectStateItem> getStates() {
        return states;
    }
}
