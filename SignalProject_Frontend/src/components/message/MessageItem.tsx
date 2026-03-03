import type { Message } from "@/types/domain";
import { formatDateTime } from "@/utils/date";

interface MessageItemProps {
  message: Message;
  isMine: boolean;
  stateLabel?: string | null;
  senderName?: string | null;
}

export function MessageItem({ message, isMine, stateLabel, senderName }: MessageItemProps) {
  const displayName = senderName?.trim() || "알 수 없음";
  const avatarText = (displayName[0] ?? "?").toUpperCase();

  return (
    <li className={`flex gap-2 ${isMine ? "justify-end" : "justify-start"}`}>
      {!isMine ? (
        <div className="mt-1 flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-white text-xs font-semibold text-slate-700 ring-1 ring-slate-200">
          {avatarText}
        </div>
      ) : null}

      <div className={`flex max-w-[86%] flex-col ${isMine ? "items-end" : "items-start"}`}>
        {!isMine ? <p className="mb-1 text-[11px] font-medium text-slate-500">{displayName}</p> : null}

        <div
          className={[
            "rounded-3xl px-4 py-2.5 text-sm leading-6",
            isMine
              ? "rounded-br-lg bg-gradient-to-r from-blue-600 to-indigo-500 text-white shadow-[0_10px_20px_rgba(59,130,246,0.18)]"
              : "rounded-bl-lg border border-slate-200 bg-white text-slate-900",
          ].join(" ")}
        >
          <p className="whitespace-pre-wrap break-words">{message.content}</p>
        </div>

        <div className={`mt-1 flex items-center gap-2 px-1 text-[11px] ${isMine ? "text-slate-500" : "text-slate-400"}`}>
          <span>{formatDateTime(message.createdAt)}</span>
          {stateLabel ? (
            <span className="rounded-full bg-blue-50 px-2 py-0.5 font-semibold text-blue-700">{stateLabel}</span>
          ) : null}
        </div>
      </div>
    </li>
  );
}
