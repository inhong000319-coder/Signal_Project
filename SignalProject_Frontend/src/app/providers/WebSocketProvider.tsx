import type { PropsWithChildren } from "react";
import { useEffect } from "react";
import { useAuthStore } from "@/features/auth/store";
import { useWebSocket } from "@/hooks/useWebSocket";

export function WebSocketProvider({ children }: PropsWithChildren) {
  const { connect, disconnect } = useWebSocket();
  const accessToken = useAuthStore((state) => state.tokenPair?.accessToken ?? null);

  useEffect(() => {
    if (accessToken) {
      connect(accessToken);
      return;
    }

    disconnect();
  }, [accessToken, connect, disconnect]);

  useEffect(() => () => disconnect(), [disconnect]);

  return <>{children}</>;
}
