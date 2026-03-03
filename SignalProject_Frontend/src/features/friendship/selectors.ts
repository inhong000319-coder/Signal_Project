import type { FriendshipStoreState } from "@/features/friendship/types";

export const selectFriends = (state: FriendshipStoreState) => state.friends;
