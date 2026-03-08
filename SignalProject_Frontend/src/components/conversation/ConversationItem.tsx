import type { ConversationSummary } from "@/types/domain";
import { formatDateTime } from "@/utils/date";

interface ConversationItemProps {
  item: ConversationSummary;
  active?: boolean;
  onClick: () => void;
}

function conversationTitle(item: ConversationSummary): string {
  if (item.type === "DIRECT") {
    return "개인 대화";
  }
  return item.roomName?.trim() || "단체 대화";
}

export function ConversationItem({ item, active = false, onClick }: ConversationItemProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={[
        "group relative w-full rounded-2xl px-3 py-3 text-left transition",
        active ? "bg-blue-50 ring-1 ring-blue-200" : "bg-transparent hover:bg-slate-50",
      ].join(" ")}
    >
      <div className="flex items-start gap-3">
        <div
          className={[
            "mt-0.5 flex h-10 w-10 shrink-0 items-center justify-center rounded-2xl text-xs font-semibold",
            active ? "bg-blue-600 text-white" : "bg-slate-100 text-slate-700",
          ].join(" ")}
        >
          {item.type === "DIRECT" ? "1:1" : "그룹"}
        </div>

        <div className="min-w-0 flex-1">
          <div className="flex items-start justify-between gap-2">
            <div className="min-w-0">
              <p className="truncate text-sm font-semibold text-slate-900">{conversationTitle(item)}</p>
              <p className="truncate text-xs text-slate-500">대화방 #{item.conversationId}</p>
            </div>

            <div className="shrink-0 text-right">
              <p className="text-[11px] text-slate-400">{formatDateTime(item.lastMessageCreatedAt)}</p>
              {item.unreadCount > 0 ? (
                <span className="mt-1 inline-flex min-w-5 items-center justify-center rounded-full bg-blue-600 px-2 py-0.5 text-[10px] font-semibold text-white">
                  {item.unreadCount}
                </span>
              ) : null}
            </div>
          </div>

          <p className="mt-1.5 line-clamp-2 text-xs leading-5 text-slate-600">
            {item.lastMessageContent ?? "아직 메시지가 없습니다."}
          </p>
        </div>
      </div>
    </button>
  );
}
