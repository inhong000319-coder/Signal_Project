import type { FriendSummary, PendingFriendRequestSummary } from "@/types/domain";

export interface FriendshipListResponseDto {
  friends: FriendSummary[];
  incomingRequests: PendingFriendRequestSummary[];
  outgoingRequests: PendingFriendRequestSummary[];
}

export interface FriendRequestDto {
  targetUserId: number;
}

export interface FriendRequestByCodeDto {
  targetUserCode: string;
}

export interface FriendshipStoreState {
  friends: FriendSummary[];
  incomingRequests: PendingFriendRequestSummary[];
  outgoingRequests: PendingFriendRequestSummary[];
  isLoading: boolean;
  error: string | null;
}
