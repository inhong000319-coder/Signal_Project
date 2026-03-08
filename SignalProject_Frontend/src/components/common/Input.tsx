import type { InputHTMLAttributes } from "react";

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string | null;
  hint?: string;
}

export function Input({ label, error, hint, className = "", ...props }: InputProps) {
  return (
    <label className="flex w-full flex-col gap-1.5 text-sm text-slate-800">
      {label ? <span className="text-xs font-semibold tracking-[0.12em] text-slate-500">{label}</span> : null}
      <input
        className={`h-11 w-full rounded-2xl border bg-white px-4 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:ring-4 focus:ring-blue-100 ${error ? "border-rose-400" : "border-slate-200"} ${className}`}
        {...props}
      />
      {error ? <span className="text-xs text-rose-600">{error}</span> : hint ? <span className="text-xs text-slate-500">{hint}</span> : null}
    </label>
  );
}