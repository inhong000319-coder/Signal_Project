import type { ConnectionPhase } from "@/websocket/frameTypes";

interface ConversationHeaderProps {
  conversationId: number | null;
  connectionPhase: ConnectionPhase;
  title?: string | null;
  memberCount?: number | null;
}

function phaseBadgeStyle(phase: ConnectionPhase): string {
  if (phase === "READY") return "border-emerald-200 bg-emerald-50 text-emerald-700";
  if (phase === "SYNCING") return "border-amber-200 bg-amber-50 text-amber-700";
  if (phase === "CONNECTED") return "border-blue-200 bg-blue-50 text-blue-700";
  if (phase === "CONNECTING") return "border-slate-200 bg-slate-50 text-slate-700";
  return "border-rose-200 bg-rose-50 text-rose-700";
}

function phaseLabel(phase: ConnectionPhase): string {
  if (phase === "READY") return "준비됨";
  if (phase === "SYNCING") return "동기화 중";
  if (phase === "CONNECTED") return "연결됨";
  if (phase === "CONNECTING") return "연결 중";
  return "연결 끊김";
}

export function ConversationHeader({ conversationId, connectionPhase, title, memberCount }: ConversationHeaderProps) {
  const resolvedTitle = title?.trim() || (conversationId ? `대화방 #${conversationId}` : "채팅");

  return (
    <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-200 bg-white px-5 py-4">
      <div className="min-w-0">
        <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">Signal</p>
        <h2 className="truncate text-lg font-semibold tracking-tight text-slate-900">{resolvedTitle}</h2>
        <div className="mt-1 flex flex-wrap items-center gap-2 text-sm text-slate-500">
          {memberCount != null ? <span>참여자 {memberCount}명</span> : null}
          <span>서버 상태가 실시간으로 동기화됩니다.</span>
        </div>
      </div>

      <span className={`inline-flex items-center rounded-full border px-3 py-1 text-xs font-semibold ${phaseBadgeStyle(connectionPhase)}`}>
        {phaseLabel(connectionPhase)}
      </span>
    </div>
  );
}
