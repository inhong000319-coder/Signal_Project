package com.signal.entry.friendship.dto;

import java.util.List;

public record FriendListResponse(
    List<FriendSummaryResponse> friends
) {
}
