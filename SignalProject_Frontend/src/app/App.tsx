import { AppRouter } from "@/app/router/AppRouter";
import { StoreProvider } from "@/app/providers/StoreProvider";
import { WebSocketProvider } from "@/app/providers/WebSocketProvider";

export function App() {
  return (
    <StoreProvider>
      <WebSocketProvider>
        <AppRouter />
      </WebSocketProvider>
    </StoreProvider>
  );
}
