import type { PropsWithChildren } from "react";
import { useEffect } from "react";
import { initializeMockData } from "@/lib/httpClient";
import { useAuthStore } from "@/features/auth/store";

export function StoreProvider({ children }: PropsWithChildren) {
  useEffect(() => {
    initializeMockData();
    useAuthStore.getState().hydrate();
  }, []);

  return <>{children}</>;
}
