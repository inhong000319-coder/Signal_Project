import type { AuthStoreState } from "@/features/auth/types";

export const selectAuthUser = (state: AuthStoreState) => state.me;
export const selectIsAuthenticated = (state: AuthStoreState) => Boolean(state.tokenPair?.accessToken);
export const selectAccessToken = (state: AuthStoreState) => state.tokenPair?.accessToken ?? null;
export const selectRefreshToken = (state: AuthStoreState) => state.tokenPair?.refreshToken ?? null;
