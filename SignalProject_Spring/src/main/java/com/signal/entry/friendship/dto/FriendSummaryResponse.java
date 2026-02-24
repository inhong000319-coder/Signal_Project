package com.signal.entry.friendship.dto;

public record FriendSummaryResponse(
    Long friendUserId,
    String nickname,
    String userCode,
    Long conversationId
) {
}
