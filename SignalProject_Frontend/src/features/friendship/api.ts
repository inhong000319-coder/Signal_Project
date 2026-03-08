import { httpClient } from "@/lib/httpClient";
import type { FriendshipListResponseDto, FriendRequestByCodeDto, FriendRequestDto } from "@/features/friendship/types";

function authHeader(accessToken: string): Record<string, string> {
  return { Authorization: `Bearer ${accessToken}` };
}

export const friendshipApi = {
  list(accessToken: string) {
    return httpClient.get<FriendshipListResponseDto>("/api/friendships", { headers: authHeader(accessToken) });
  },
  request(accessToken: string, payload: FriendRequestDto) {
    return httpClient.post("/api/friendships", payload, { headers: authHeader(accessToken) });
  },
  requestByCode(accessToken: string, payload: FriendRequestByCodeDto) {
    return httpClient.post("/api/friendships/by-code", payload, { headers: authHeader(accessToken) });
  },
  accept(accessToken: string, requesterUserId: number) {
    return httpClient.post(`/api/friendships/${requesterUserId}/accept`, undefined, { headers: authHeader(accessToken) });
  },
  block(accessToken: string, targetUserId: number) {
    return httpClient.post(`/api/friendships/${targetUserId}/block`, undefined, { headers: authHeader(accessToken) });
  },
  remove(accessToken: string, targetUserId: number) {
    return httpClient.post<void>(`/api/friendships/${targetUserId}/remove`, undefined, { headers: authHeader(accessToken) });
  },
};