package com.signal.entry.message.dto;

import java.util.List;

public record MessageListResponse(
    List<MessageListItemResponse> messages,
    Long nextBeforeMessageId,
    Long lastDeliveredMessageId,
    Long lastReadMessageId,
    long unreadCount
) {
}
