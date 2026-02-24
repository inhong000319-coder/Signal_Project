package com.signal.entry.friendship.dto;

import jakarta.validation.constraints.NotNull;

public record FriendRequestCreate(
    @NotNull
    Long targetUserId
) {
}
