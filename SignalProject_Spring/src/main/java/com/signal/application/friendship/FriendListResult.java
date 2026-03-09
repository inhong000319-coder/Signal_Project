package com.signal.application.friendship;

import java.util.List;

public final class FriendListResult {
    private final List<FriendSummary> friends;

    public FriendListResult(List<FriendSummary> friends) {
        this.friends = friends;
    }

    public List<FriendSummary> getFriends() {
        return friends;
    }
}
