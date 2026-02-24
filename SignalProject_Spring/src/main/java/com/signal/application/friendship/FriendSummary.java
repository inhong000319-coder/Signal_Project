package com.signal.application.friendship;

public final class FriendSummary {
    private final Long friendUserId;
    private final String nickname;
    private final String userCode;
    private final Long conversationId;

    public FriendSummary(Long friendUserId, String nickname, String userCode, Long conversationId) {
        this.friendUserId = friendUserId;
        this.nickname = nickname;
        this.userCode = userCode;
        this.conversationId = conversationId;
    }

    public Long getFriendUserId() {
        return friendUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUserCode() {
        return userCode;
    }

    public Long getConversationId() {
        return conversationId;
    }
}
