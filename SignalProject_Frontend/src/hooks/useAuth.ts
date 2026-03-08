import { useCallback, useMemo } from 'react';
import { authApi } from '@/features/auth/api';
import { useAuthStore } from '@/features/auth/store';

export function useAuth() {
  const auth = useAuthStore();

  const hydrate = useCallback(() => {
    useAuthStore.getState().hydrate();
  }, []);

  const fetchMe = useCallback(async () => {
    const accessToken = useAuthStore.getState().tokenPair?.accessToken;
    if (!accessToken) return null;
    const me = await authApi.me(accessToken);
    useAuthStore.getState().setMe(me);
    return me;
  }, []);

  const login = useCallback(async (loginId: string, password: string) => {
    const store = useAuthStore.getState();
    store.setLoading(true);
    store.setError(null);
    try {
      const tokenPair = await authApi.login({ loginId, password });
      store.setSession(tokenPair, null);
      const me = await authApi.me(tokenPair.accessToken);
      store.setSession(tokenPair, me);
      return me;
    } catch (error) {
      const message = (error as { message?: string }).message ?? '로그인 실패';
      store.setError(message);
      throw error;
    } finally {
      store.setLoading(false);
    }
  }, []);

  const register = useCallback(async (loginId: string, password: string, nickname: string) => {
    const store = useAuthStore.getState();
    store.setLoading(true);
    store.setError(null);
    try {
      return await authApi.register({ loginId, password, nickname });
    } catch (error) {
      const message = (error as { message?: string }).message ?? '회원가입 실패';
      store.setError(message);
      throw error;
    } finally {
      store.setLoading(false);
    }
  }, []);

  const logout = useCallback(async () => {
    const store = useAuthStore.getState();
    const accessToken = store.tokenPair?.accessToken;
    const refreshToken = store.tokenPair?.refreshToken;
    try {
      if (accessToken && refreshToken) {
        await authApi.logout(accessToken, { refreshToken });
      }
    } finally {
      store.clearSession();
    }
  }, []);

  const refresh = useCallback(async () => {
    const store = useAuthStore.getState();
    const refreshToken = store.tokenPair?.refreshToken;
    if (!refreshToken) {
      store.clearSession();
      return null;
    }
    const tokenPair = await authApi.refresh({ refreshToken });
    store.setSession(tokenPair, store.me);
    return tokenPair;
  }, []);

  const changePassword = useCallback(async (currentPassword: string, newPassword: string) => {
    const store = useAuthStore.getState();
    const accessToken = store.tokenPair?.accessToken;
    if (!accessToken) throw new Error('인증이 필요합니다.');
    await authApi.changePassword(accessToken, { currentPassword, newPassword });
    await logout();
  }, [logout]);

  return useMemo(
    () => ({
      ...auth,
      hydrate,
      fetchMe,
      login,
      register,
      logout,
      refresh,
      changePassword,
    }),
    [auth, hydrate, fetchMe, login, register, logout, refresh, changePassword]
  );
}