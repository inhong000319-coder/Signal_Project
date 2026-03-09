package com.signal.entry.friendship.dto;

import java.time.Instant;

public record FriendshipResponse(
    Long requesterUserId,
    Long targetUserId,
    String status,
    Instant requestedAt,
    Instant acceptedAt
) {
}
