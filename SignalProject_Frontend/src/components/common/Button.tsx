import type { ButtonHTMLAttributes, PropsWithChildren } from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "ghost" | "danger";
  size?: "sm" | "md" | "lg" | "icon";
}

export function Button({
  children,
  variant = "primary",
  size = "md",
  className = "",
  ...props
}: PropsWithChildren<ButtonProps>) {
  const variantStyle =
    variant === "primary"
      ? "border-transparent bg-gradient-to-r from-blue-600 to-cyan-500 text-white shadow-[0_10px_22px_rgba(37,99,235,0.22)] hover:from-blue-700 hover:to-cyan-600 hover:shadow-[0_14px_28px_rgba(37,99,235,0.28)]"
      : variant === "secondary"
        ? "border-slate-200 bg-white text-slate-700 shadow-sm hover:bg-slate-50"
        : variant === "danger"
          ? "border-transparent bg-gradient-to-r from-rose-600 to-rose-500 text-white shadow-[0_10px_20px_rgba(225,29,72,0.18)] hover:from-rose-700 hover:to-rose-600"
          : "border-transparent bg-transparent text-slate-600 hover:bg-slate-100 hover:text-slate-900";

  const sizeStyle =
    size === "sm"
      ? "h-9 rounded-xl px-3 text-xs"
      : size === "lg"
        ? "h-12 rounded-2xl px-5 text-sm"
        : size === "icon"
          ? "h-10 w-10 rounded-xl p-0"
          : "h-10 rounded-xl px-4 text-sm";

  return (
    <button
      className={`inline-flex items-center justify-center gap-1 border font-medium transition disabled:cursor-not-allowed disabled:opacity-50 ${variantStyle} ${sizeStyle} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}