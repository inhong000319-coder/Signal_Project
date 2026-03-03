import { Button } from "@/components/common/Button";
import type { FriendSummary } from "@/types/domain";

interface MemberListProps {
  friends: FriendSummary[];
  onStartDirect: (friendUserId: number) => void;
  onRemoveFriend?: (friendUserId: number) => void;
  removingFriendUserId?: number | null;
}

export function MemberList({
  friends,
  onStartDirect,
  onRemoveFriend,
  removingFriendUserId = null,
}: MemberListProps) {
  return (
    <div className="rounded-[28px] border border-slate-200/80 bg-white p-4 shadow-[0_20px_40px_rgba(15,23,42,0.06)]">
      <div className="mb-4 flex items-center justify-between gap-2">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-400">친구</p>
          <h3 className="text-lg font-semibold text-slate-900">수락된 친구</h3>
        </div>
        <span className="rounded-full border border-slate-200 bg-slate-50 px-3 py-1 text-xs text-slate-600">{friends.length}명</span>
      </div>

      <ul className="signal-scroll max-h-[520px] space-y-2 overflow-y-auto pr-1">
        {friends.map((friend) => (
          <li
            key={friend.friendUserId}
            className="flex items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-slate-50/70 px-3 py-3"
          >
            <div className="flex min-w-0 items-center gap-3">
              <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-white text-sm font-semibold text-slate-700 ring-1 ring-slate-200">
                {friend.nickname.slice(0, 1).toUpperCase()}
              </div>
              <div className="min-w-0">
                <p className="truncate text-sm font-semibold text-slate-900">{friend.nickname}</p>
                <p className="truncate text-xs text-slate-500">친구 코드 {friend.userCode}</p>
                {friend.conversationId ? (
                  <p className="text-[11px] text-blue-600">기존 대화방 #{friend.conversationId}</p>
                ) : null}
              </div>
            </div>

            <div className="flex shrink-0 items-center gap-2">
              <Button size="sm" variant="secondary" onClick={() => onStartDirect(friend.friendUserId)}>
                대화 시작
              </Button>
              {onRemoveFriend ? (
                <Button
                  size="sm"
                  variant="danger"
                  onClick={() => onRemoveFriend(friend.friendUserId)}
                  disabled={removingFriendUserId === friend.friendUserId}
                >
                  {removingFriendUserId === friend.friendUserId ? "삭제 중..." : "친구 삭제"}
                </Button>
              ) : null}
            </div>
          </li>
        ))}

        {friends.length === 0 ? (
          <li className="rounded-2xl border border-dashed border-slate-300 bg-slate-50 p-8 text-center text-sm text-slate-500">
            아직 수락된 친구가 없습니다. 왼쪽에서 친구 코드를 입력해 요청을 보내보세요.
          </li>
        ) : null}
      </ul>
    </div>
  );
}