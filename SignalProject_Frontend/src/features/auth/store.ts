import { create } from "zustand";
import { STORAGE_KEYS } from "@/lib/constants";
import type { AuthStoreState } from "@/features/auth/types";
import type { AuthUser, TokenPair } from "@/types/domain";

interface AuthStore extends AuthStoreState {
  setLoading: (isLoading: boolean) => void;
  setError: (error: string | null) => void;
  setSession: (tokenPair: TokenPair, me?: AuthUser | null) => void;
  setMe: (me: AuthUser | null) => void;
  clearSession: () => void;
  hydrate: () => void;
}

interface PersistedAuth {
  tokenPair: TokenPair | null;
  me: AuthUser | null;
}

function getSessionStorage(): Storage | null {
  if (typeof window === "undefined") return null;
  try {
    return window.sessionStorage;
  } catch {
    return null;
  }
}

function getLocalStorage(): Storage | null {
  if (typeof window === "undefined") return null;
  try {
    return window.localStorage;
  } catch {
    return null;
  }
}

function readPersisted(): PersistedAuth | null {
  const session = getSessionStorage();
  const local = getLocalStorage();

  const sessionRaw = session?.getItem(STORAGE_KEYS.auth);
  if (sessionRaw) {
    try {
      return JSON.parse(sessionRaw) as PersistedAuth;
    } catch {
      session?.removeItem(STORAGE_KEYS.auth);
    }
  }

  // Backward-compatibility: migrate legacy localStorage auth into tab-scoped sessionStorage once.
  const localRaw = local?.getItem(STORAGE_KEYS.auth);
  if (!localRaw) return null;

  try {
    const parsed = JSON.parse(localRaw) as PersistedAuth;
    session?.setItem(STORAGE_KEYS.auth, JSON.stringify(parsed));
    local?.removeItem(STORAGE_KEYS.auth);
    return parsed;
  } catch {
    local?.removeItem(STORAGE_KEYS.auth);
    return null;
  }
}

function writePersisted(payload: PersistedAuth): void {
  const session = getSessionStorage();
  if (!session) return;

  session.setItem(STORAGE_KEYS.auth, JSON.stringify(payload));
  // Keep auth isolated per tab/window; clear legacy shared storage.
  getLocalStorage()?.removeItem(STORAGE_KEYS.auth);
}

export const useAuthStore = create<AuthStore>((set) => ({
  tokenPair: null,
  me: null,
  isLoading: false,
  error: null,
  setLoading: (isLoading) => set({ isLoading }),
  setError: (error) => set({ error }),
  setSession: (tokenPair, me) => {
    writePersisted({ tokenPair, me: me ?? null });
    set({ tokenPair, me: me ?? null, error: null });
  },
  setMe: (me) => {
    set((state) => {
      writePersisted({ tokenPair: state.tokenPair, me });
      return { me, error: null };
    });
  },
  clearSession: () => {
    getSessionStorage()?.removeItem(STORAGE_KEYS.auth);
    getLocalStorage()?.removeItem(STORAGE_KEYS.auth);
    set({ tokenPair: null, me: null, error: null, isLoading: false });
  },
  hydrate: () => {
    const persisted = readPersisted();
    if (!persisted) return;
    set({ tokenPair: persisted.tokenPair, me: persisted.me });
  },
}));
