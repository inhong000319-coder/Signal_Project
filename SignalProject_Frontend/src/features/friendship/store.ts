import { create } from "zustand";
import type { FriendSummary } from "@/types/domain";
import type { FriendshipListResponseDto, FriendshipStoreState } from "@/features/friendship/types";

interface FriendshipStore extends FriendshipStoreState {
  setSnapshot: (payload: FriendshipListResponseDto) => void;
  setFriends: (friends: FriendSummary[]) => void;
  upsertFriend: (friend: FriendSummary) => void;
  setLoading: (isLoading: boolean) => void;
  setError: (error: string | null) => void;
}

export const useFriendshipStore = create<FriendshipStore>((set) => ({
  friends: [],
  incomingRequests: [],
  outgoingRequests: [],
  isLoading: false,
  error: null,
  setSnapshot: (payload) =>
    set({
      friends: [...payload.friends].sort((a, b) => a.nickname.localeCompare(b.nickname, "ko")),
      incomingRequests: payload.incomingRequests,
      outgoingRequests: payload.outgoingRequests,
      error: null,
    }),
  setFriends: (friends) => set({ friends, error: null }),
  upsertFriend: (friend) =>
    set((state) => {
      const next = state.friends.filter((item) => item.friendUserId !== friend.friendUserId);
      next.push(friend);
      next.sort((a, b) => a.nickname.localeCompare(b.nickname, "ko"));
      return { friends: next };
    }),
  setLoading: (isLoading) => set({ isLoading }),
  setError: (error) => set({ error }),
}));
