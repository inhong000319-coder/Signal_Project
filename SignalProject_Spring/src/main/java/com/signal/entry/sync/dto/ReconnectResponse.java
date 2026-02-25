package com.signal.entry.sync.dto;

import java.util.List;

public record ReconnectResponse(
    Long conversationId,
    Long userId,
    Long effectiveLastDeliveredMessageId,
    Long effectiveLastReadMessageId,
    Long serverLastDeliveredMessageId,
    Long serverLastReadMessageId,
    List<ReconnectMessageResponse> messages,
    List<ReconnectStateResponse> states
) {
}
