import type { HTMLAttributes, PropsWithChildren } from "react";

interface CardProps extends HTMLAttributes<HTMLDivElement> {}

export function Card({ children, className = "", ...props }: PropsWithChildren<CardProps>) {
  return (
    <div className={`rounded-2xl border border-slate-200/80 bg-white/90 p-4 shadow-[0_16px_40px_rgba(15,23,42,0.06)] backdrop-blur ${className}`} {...props}>
      {children}
    </div>
  );
}