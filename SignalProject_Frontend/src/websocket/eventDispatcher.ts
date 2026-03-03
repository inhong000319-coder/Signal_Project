import type { ServerEventFrame } from "@/types/frame";

export type ServerEventListener = (frame: ServerEventFrame) => void;

class LocalEventDispatcher {
  private listeners = new Set<ServerEventListener>();

  subscribe(listener: ServerEventListener): () => void {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  emit(frame: ServerEventFrame): void {
    this.listeners.forEach((listener) => listener(frame));
  }
}

export const serverEventDispatcher = new LocalEventDispatcher();
