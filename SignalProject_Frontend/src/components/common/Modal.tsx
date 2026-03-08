import type { PropsWithChildren, ReactNode } from "react";
import { Button } from "@/components/common/Button";

interface ModalProps extends PropsWithChildren {
  open: boolean;
  title: string;
  onClose: () => void;
  footer?: ReactNode;
}

export function Modal({ open, title, onClose, footer, children }: ModalProps) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30 p-4">
      <div className="w-full max-w-lg rounded-xl border border-signal-line bg-white p-4 shadow-panel">
        <div className="mb-3 flex items-center justify-between">
          <h3 className="text-lg font-semibold">{title}</h3>
          <Button variant="ghost" onClick={onClose}>닫기</Button>
        </div>
        <div className="mb-4">{children}</div>
        {footer ? <div className="flex justify-end gap-2">{footer}</div> : null}
      </div>
    </div>
  );
}
