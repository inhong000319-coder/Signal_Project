import type { AuthUser, TokenPair } from "@/types/domain";

export interface LoginRequestDto {
  loginId: string;
  password: string;
}

export interface RegisterRequestDto {
  loginId: string;
  password: string;
  nickname: string;
}

export interface RegisterResponseDto {
  userId: number;
  userCode: string;
}

export interface RefreshRequestDto {
  refreshToken: string;
}

export interface LogoutRequestDto {
  refreshToken: string;
}

export interface ChangePasswordRequestDto {
  currentPassword: string;
  newPassword: string;
}

export interface AuthStoreState {
  tokenPair: TokenPair | null;
  me: AuthUser | null;
  isLoading: boolean;
  error: string | null;
}
