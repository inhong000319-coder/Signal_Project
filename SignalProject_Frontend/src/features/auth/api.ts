import { httpClient } from '@/lib/httpClient';
import type { AuthUser, TokenPair } from '@/types/domain';
import type {
  ChangePasswordRequestDto,
  LoginRequestDto,
  LogoutRequestDto,
  RefreshRequestDto,
  RegisterRequestDto,
  RegisterResponseDto,
} from '@/features/auth/types';

function authHeader(accessToken?: string | null): Record<string, string> | undefined {
  return accessToken ? { Authorization: `Bearer ${accessToken}` } : undefined;
}

export const authApi = {
  register(payload: RegisterRequestDto) {
    return httpClient.post<RegisterResponseDto>('/api/users', payload);
  },
  login(payload: LoginRequestDto) {
    return httpClient.post<TokenPair>('/api/auth/login', payload);
  },
  refresh(payload: RefreshRequestDto) {
    return httpClient.post<TokenPair>('/api/auth/refresh', payload);
  },
  logout(accessToken: string, payload: LogoutRequestDto) {
    return httpClient.post<void>('/api/auth/logout', payload, { headers: authHeader(accessToken) });
  },
  me(accessToken: string) {
    return httpClient.get<AuthUser>('/api/auth/me', { headers: authHeader(accessToken) });
  },
  changePassword(accessToken: string, payload: ChangePasswordRequestDto) {
    return httpClient.post<void>('/api/users/me/password', payload, {
      headers: authHeader(accessToken),
    });
  },
};