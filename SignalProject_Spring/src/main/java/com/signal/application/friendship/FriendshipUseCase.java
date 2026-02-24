package com.signal.application.friendship;

public interface FriendshipUseCase {
    FriendshipResult request(FriendRequestCommand command);
    FriendshipResult accept(FriendAcceptCommand command);
    FriendshipResult block(FriendBlockCommand command);
}
