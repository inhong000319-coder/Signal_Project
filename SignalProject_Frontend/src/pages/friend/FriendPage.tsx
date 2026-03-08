import { useEffect, useState } from "react";
import type { ReactNode } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { Button } from "@/components/common/Button";
import { Card } from "@/components/common/Card";
import { Input } from "@/components/common/Input";
import { MemberList } from "@/components/conversation/MemberList";
import { useAuthStore } from "@/features/auth/store";
import { friendshipApi } from "@/features/friendship/api";
import { useFriendshipStore } from "@/features/friendship/store";
import { useAuth } from "@/hooks/useAuth";
import { useConversation } from "@/hooks/useConversation";
import type { PendingFriendRequestSummary } from "@/types/domain";
import { formatDateTime } from "@/utils/date";

export function FriendPage() {
  const navigate = useNavigate();
  const auth = useAuthStore();
  const friendship = useFriendshipStore();
  const { createDirectConversation } = useConversation();
  const { logout } = useAuth();
  const [friendInput, setFriendInput] = useState("");
  const [actionMessage, setActionMessage] = useState<string | null>(null);
  const [removingFriendUserId, setRemovingFriendUserId] = useState<number | null>(null);
  const accessToken = auth.tokenPair?.accessToken ?? null;

  useEffect(() => {
    if (!accessToken) return;
    void refreshFriendshipList();
  }, [accessToken]);
  async function refreshFriendshipList() {
    if (!accessToken) return;
    friendship.setLoading(true);
    try {
      const res = await friendshipApi.list(accessToken);
      friendship.setSnapshot(res);
    } catch (e) {
      friendship.setError((e as { message?: string }).message ?? "친구 목록을 불러오지 못했습니다.");
    } finally {
      friendship.setLoading(false);
    }
  }

  
  async function copyTextWithFallback(text: string): Promise<boolean> {
    if (typeof navigator !== "undefined" && navigator.clipboard?.writeText) {
      try {
        await navigator.clipboard.writeText(text);
        return true;
      } catch {
        // fall through to legacy copy path
      }
    }

    if (typeof document === "undefined") {
      return false;
    }

    const textarea = document.createElement("textarea");
    textarea.value = text;
    textarea.setAttribute("readonly", "true");
    textarea.style.position = "fixed";
    textarea.style.opacity = "0";
    textarea.style.left = "-9999px";
    document.body.appendChild(textarea);
    textarea.focus();
    textarea.select();

    let copied = false;
    try {
      copied = document.execCommand("copy");
    } catch {
      copied = false;
    } finally {
      document.body.removeChild(textarea);
    }

    return copied;
  }

  
  async function copyMyCode() {
    const code = auth.me?.userCode;
    if (!code) return;
    const copied = await copyTextWithFallback(code);
    setActionMessage(copied ? "친구 코드를 복사했습니다." : "친구 코드 복사에 실패했습니다.");
  }
  
  async function sendFriendRequest() {
    if (!accessToken) return;

    const trimmed = friendInput.replace(/\s+/g, "").trim();
    if (!trimmed) {
      setActionMessage("친구 코드를 입력해 주세요.");
      return;
    }

    // Compatibility fallback: numeric userId requests are still supported for older backend/testing flows.
    const parsedId = Number(trimmed);
    const isNumericId = Number.isInteger(parsedId) && parsedId > 0;

    try {
      if (isNumericId) {
        await friendshipApi.request(accessToken, { targetUserId: parsedId });
      } else {
        await friendshipApi.requestByCode(accessToken, { targetUserCode: trimmed });
      }
      setFriendInput("");
      setActionMessage("친구 요청을 보냈습니다.");
      await refreshFriendshipList();
    } catch (e) {
      setActionMessage((e as { message?: string }).message ?? "친구 요청에 실패했습니다.");
    }
  }

  
  async function acceptRequest(requesterUserId: number) {
    if (!accessToken) return;
    try {
      await friendshipApi.accept(accessToken, requesterUserId);
      setActionMessage("친구 요청을 수락했습니다.");
      await refreshFriendshipList();
    } catch (e) {
      setActionMessage((e as { message?: string }).message ?? "요청 수락에 실패했습니다.");
    }
  }

  
  async function blockUser(targetUserId: number) {
    if (!accessToken) return;
    try {
      await friendshipApi.block(accessToken, targetUserId);
      setActionMessage("사용자를 차단했습니다.");
      await refreshFriendshipList();
    } catch (e) {
      setActionMessage((e as { message?: string }).message ?? "차단 처리에 실패했습니다.");
    }
  }

  
  async function removeFriend(friendUserId: number) {
    if (!accessToken) return;

    if (typeof window !== "undefined") {
      const ok = window.confirm("정말 이 친구를 삭제하시겠습니까?");
      if (!ok) return;
    }

    setRemovingFriendUserId(friendUserId);
    try {
      await friendshipApi.remove(accessToken, friendUserId);
      setActionMessage("친구를 삭제했습니다.");
      await refreshFriendshipList();
    } catch (e) {
      setActionMessage((e as { message?: string }).message ?? "친구 삭제에 실패했습니다.");
    } finally {
      setRemovingFriendUserId(null);
    }
  }

  if (!accessToken) {
    return <Navigate to="/login" replace />;
  }

  return (
    <main className="signal-shell-wide">
      <div className="space-y-4">
        <section className="grid gap-4 xl:grid-cols-[1.35fr_0.95fr]">
          <div className="relative overflow-hidden rounded-[30px] bg-gradient-to-br from-blue-700 via-blue-600 to-cyan-500 p-6 text-white shadow-[0_28px_60px_rgba(37,99,235,0.24)]">
            <div className="pointer-events-none absolute -right-10 -top-10 h-40 w-40 rounded-full bg-white/10 blur-2xl" />
            <div className="pointer-events-none absolute -bottom-12 left-8 h-32 w-32 rounded-full bg-cyan-200/20 blur-2xl" />

            <p className="text-xs font-semibold uppercase tracking-[0.2em] text-white/80">친구</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-tight">친구를 추가하고 대화를 시작하세요</h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-white/90">
              먼저 내 친구 코드를 공유하세요. 요청이 수락되면 바로 개인 대화를 시작할 수 있습니다.
            </p>

            <div className="mt-6 grid gap-3 sm:grid-cols-[minmax(0,1fr)_auto] sm:items-center">
              <div className="rounded-2xl border border-white/20 bg-white/10 p-4 backdrop-blur">
                <p className="text-xs font-semibold uppercase tracking-[0.14em] text-white/70">내 친구 코드</p>
                <div className="mt-2 flex flex-wrap items-center gap-2">
                  <code className="rounded-xl bg-white px-4 py-2 font-mono text-base font-semibold tracking-[0.14em] text-slate-900">
                    {auth.me?.userCode ?? "-"}
                  </code>
                  <Button variant="secondary" onClick={() => void copyMyCode()}>
                    코드 복사
                  </Button>
                </div>
                <p className="mt-2 text-xs text-white/80">상대방에게 이 코드를 전달해 친구 추가에 사용하세요.</p>
              </div>

              <div className="flex flex-col gap-2 sm:min-w-40">
                <Link to="/conversations">
                  <Button variant="secondary" className="w-full">대화 목록</Button>
                </Link>
                <Button
                  variant="ghost"
                  className="w-full border border-white/20 bg-white/10 text-white hover:bg-white/15"
                  onClick={() => void logout()}
                >
                  로그아웃
                </Button>
              </div>
            </div>
          </div>

          <Card className="rounded-[30px] p-5">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">프로필</p>
                <h2 className="mt-1 text-xl font-semibold text-slate-900">{auth.me?.nickname ?? auth.me?.loginId}</h2>
                <p className="mt-1 text-sm text-slate-500">{auth.me?.loginId}</p>
              </div>
              <span className="rounded-full border border-slate-200 bg-slate-50 px-3 py-1 text-xs text-slate-600">
                친구 {friendship.friends.length}명
              </span>
            </div>

            <div className="mt-4 grid gap-3 sm:grid-cols-2">
              <div className="rounded-2xl border border-slate-200 bg-slate-50/80 p-4">
                <p className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-400">닉네임</p>
                <p className="mt-1 text-sm font-semibold text-slate-900">{auth.me?.nickname ?? "-"}</p>
              </div>
              <div className="rounded-2xl border border-slate-200 bg-slate-50/80 p-4">
                <p className="text-xs font-semibold uppercase tracking-[0.14em] text-slate-400">로그인 ID</p>
                <p className="mt-1 text-sm font-semibold text-slate-900">{auth.me?.loginId ?? "-"}</p>
              </div>
            </div>

            {actionMessage ? (
              <p className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700">{actionMessage}</p>
            ) : null}
            {friendship.error ? (
              <p className="mt-3 rounded-2xl border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{friendship.error}</p>
            ) : null}
          </Card>
        </section>

        <section className="grid gap-4 xl:grid-cols-[420px_minmax(0,1fr)]">
          <div className="space-y-4">
            <Card className="rounded-[30px] p-5">
              <div>
                <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">친구 추가</p>
                <h2 className="mt-1 text-xl font-semibold text-slate-900">친구 요청 보내기</h2>
                <p className="mt-2 text-sm text-slate-500">상대방이 공유한 친구 코드를 입력해 요청을 보내세요.</p>
              </div>

              <div className="mt-5 space-y-4">
                <Input
                  label="친구 코드"
                  value={friendInput}
                  onChange={(e) => setFriendInput(e.target.value)}
                  placeholder="예: &7$bmc"
                  hint="친구 코드(6자리)를 입력하면 됩니다."
                />

                <Button onClick={() => void sendFriendRequest()} disabled={friendship.isLoading}>
                  친구 요청 보내기
                </Button>

                {friendship.isLoading ? <p className="text-xs text-slate-500">친구 상태를 동기화하는 중...</p> : null}
              </div>
            </Card>

            <PendingRequestSection
              title="받은 요청"
              badgeLabel={`${friendship.incomingRequests.length}`}
              emptyMessage="받은 친구 요청이 없습니다."
              requests={friendship.incomingRequests}
              actionSlot={(request) => (
                <div className="flex items-center gap-2">
                  <Button size="sm" variant="secondary" onClick={() => void acceptRequest(request.requesterUserId)}>
                    수락
                  </Button>
                  <Button size="sm" variant="danger" onClick={() => void blockUser(request.counterpartUserId)}>
                    차단
                  </Button>
                </div>
              )}
            />

            <PendingRequestSection
              title="보낸 요청"
              badgeLabel={`${friendship.outgoingRequests.length}`}
              emptyMessage="보낸 친구 요청이 없습니다."
              requests={friendship.outgoingRequests}
              actionSlot={() => <span className="text-xs text-slate-500">대기 중</span>}
            />
          </div>

          <MemberList
            friends={friendship.friends}
            onStartDirect={async (friendUserId) => {
              const detail = await createDirectConversation(friendUserId);
              navigate(`/conversations/${detail.conversationId}`);
            }}
            onRemoveFriend={(friendUserId) => {
              void removeFriend(friendUserId);
            }}
            removingFriendUserId={removingFriendUserId}
          />
        </section>
      </div>
    </main>
  );
}

interface PendingRequestSectionProps {
  title: string;
  badgeLabel: string;
  emptyMessage: string;
  requests: PendingFriendRequestSummary[];
  actionSlot: (request: PendingFriendRequestSummary) => ReactNode;
}

function PendingRequestSection({
  title,
  badgeLabel,
  emptyMessage,
  requests,
  actionSlot,
}: PendingRequestSectionProps) {
  return (
    <Card className="rounded-[30px] p-5">
      <div className="mb-4 flex items-center justify-between gap-2">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">요청</p>
          <h3 className="text-lg font-semibold text-slate-900">{title}</h3>
        </div>
        <span className="rounded-full border border-slate-200 bg-slate-50 px-3 py-1 text-xs text-slate-600">{badgeLabel}</span>
      </div>

      {requests.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-slate-50 px-4 py-6 text-center text-sm text-slate-500">
          {emptyMessage}
        </div>
      ) : (
        <ul className="space-y-2">
          {requests.map((request) => (
            <li
              key={`${request.requesterUserId}-${request.targetUserId}`}
              className="flex items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-slate-50/70 px-3 py-3"
            >
              <div className="min-w-0">
                <p className="truncate text-sm font-semibold text-slate-900">{request.counterpartNickname}</p>
                <p className="truncate text-xs text-slate-500">친구 코드 {request.counterpartUserCode}</p>
                <p className="text-[11px] text-slate-400">요청 시간 {formatDateTime(request.requestedAt)}</p>
              </div>
              <div className="shrink-0">{actionSlot(request)}</div>
            </li>
          ))}
        </ul>
      )}
    </Card>
  );
}
