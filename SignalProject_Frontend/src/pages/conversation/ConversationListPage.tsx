import { useEffect, useMemo, useState } from "react";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { Button } from "@/components/common/Button";
import { Card } from "@/components/common/Card";
import { Input } from "@/components/common/Input";
import { Modal } from "@/components/common/Modal";
import { ConversationItem } from "@/components/conversation/ConversationItem";
import { friendshipApi } from "@/features/friendship/api";
import { useConversation } from "@/hooks/useConversation";
import { useAuthStore } from "@/features/auth/store";
import { useAuth } from "@/hooks/useAuth";
import type { FriendSummary } from "@/types/domain";
import { useSyncStore } from "@/features/sync/store";
import { selectConnectionPhase } from "@/features/sync/selectors";

function phaseLabel(phase: string): string {
  if (phase === "READY") return "준비됨";
  if (phase === "SYNCING") return "동기화 중";
  if (phase === "CONNECTED") return "연결됨";
  if (phase === "CONNECTING") return "연결 중";
  return "연결 끊김";
}

export function ConversationListPage() {
  const navigate = useNavigate();
  const auth = useAuthStore();
  const { logout } = useAuth();
  const {
    conversations,
    isLoading,
    error,
    loadConversations,
    createDirectConversation,
    createGroupConversation,
  } = useConversation();
  const [keyword, setKeyword] = useState("");
  const [uiMessage, setUiMessage] = useState<string | null>(null);
  const connectionPhase = useSyncStore(selectConnectionPhase);

  const [groupModalOpen, setGroupModalOpen] = useState(false);
  const [groupCandidates, setGroupCandidates] = useState<FriendSummary[]>([]);
  const [selectedFriendIds, setSelectedFriendIds] = useState<number[]>([]);
  const [groupRoomName, setGroupRoomName] = useState("");
  const [groupLoading, setGroupLoading] = useState(false);
  const [groupSubmitting, setGroupSubmitting] = useState(false);
  const [groupError, setGroupError] = useState<string | null>(null);

  useEffect(() => {
    if (!auth.tokenPair?.accessToken) return;
    void loadConversations().catch(() => undefined);
  }, [auth.tokenPair?.accessToken, loadConversations]);

  const filtered = useMemo(() => {
    const q = keyword.trim().toLowerCase();
    if (!q) return conversations;
    return conversations.filter((item) => {
      const haystack = [
        String(item.conversationId),
        item.type,
        item.roomName ?? "",
        item.lastMessageContent ?? "",
      ].join(" ").toLowerCase();
      return haystack.includes(q);
    });
  }, [keyword, conversations]);

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
    setUiMessage(copied ? "친구 코드를 복사했습니다." : "친구 코드 복사에 실패했습니다.");
  }

  async function openCreateChatModal() {
    const accessToken = auth.tokenPair?.accessToken;
    if (!accessToken) return;

    setGroupModalOpen(true);
    setGroupLoading(true);
    setGroupError(null);
    setSelectedFriendIds([]);
    setGroupRoomName("");

    try {
      const response = await friendshipApi.list(accessToken);
      setGroupCandidates(response.friends);
    } catch (e) {
      setGroupError((e as { message?: string }).message ?? "친구 목록을 불러오지 못했습니다.");
    } finally {
      setGroupLoading(false);
    }
  }

  function toggleGroupMember(friendUserId: number) {
    setSelectedFriendIds((prev) =>
      prev.includes(friendUserId) ? prev.filter((id) => id !== friendUserId) : [...prev, friendUserId]
    );
  }

  async function submitGroupConversation() {
    if (selectedFriendIds.length === 0) {
      setGroupError("친구를 1명 이상 선택해 주세요.");
      return;
    }

    setGroupSubmitting(true);
    setGroupError(null);
    try {
      const detail =
        selectedFriendIds.length === 1
          ? await createDirectConversation(selectedFriendIds[0])
          : await createGroupConversation(selectedFriendIds, groupRoomName);
      setGroupModalOpen(false);
      setSelectedFriendIds([]);
      setGroupRoomName("");
      navigate(`/conversations/${detail.conversationId}`);
    } catch (e) {
      setGroupError((e as { message?: string }).message ?? "대화방 생성에 실패했습니다.");
    } finally {
      setGroupSubmitting(false);
    }
  }

  if (!auth.tokenPair?.accessToken) {
    return <Navigate to="/login" replace />;
  }

  return (
    <main className="signal-shell-wide">
      <div className="space-y-4">
        <section className="grid gap-4 xl:grid-cols-[1.3fr_1fr]">
          <div className="relative overflow-hidden rounded-[30px] bg-gradient-to-br from-slate-900 via-slate-800 to-blue-900 p-6 text-white shadow-[0_28px_60px_rgba(15,23,42,0.28)]">
            <div className="pointer-events-none absolute -right-10 -top-12 h-40 w-40 rounded-full bg-blue-300/20 blur-2xl" />
            <div className="pointer-events-none absolute bottom-0 left-6 h-24 w-24 rounded-full bg-cyan-200/20 blur-2xl" />

            <p className="text-xs font-semibold uppercase tracking-[0.18em] text-white/70">Signal</p>
            <h1 className="mt-2 text-3xl font-semibold tracking-tight">대화 목록</h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-white/85">
              최근 대화를 확인하고 새 대화방을 만들어 바로 대화를 시작하세요.
            </p>

            <div className="mt-5 flex flex-wrap gap-2">
              <span className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3 py-1 text-xs">
                <span className="signal-dot-online" /> {phaseLabel(connectionPhase)}
              </span>
              <span className="rounded-full border border-white/15 bg-white/10 px-3 py-1 text-xs">대화방 {conversations.length}</span>
              <span className="rounded-full border border-white/15 bg-white/10 px-3 py-1 text-xs">검색 결과 {filtered.length}</span>
            </div>
          </div>

          <Card className="rounded-[30px] p-5">
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">계정</p>
                <h2 className="mt-1 text-xl font-semibold text-slate-900">{auth.me?.nickname ?? auth.me?.loginId}</h2>
                <p className="mt-1 text-sm text-slate-500">{auth.me?.loginId}</p>
              </div>
              <Button variant="ghost" onClick={() => void logout()}>로그아웃</Button>
            </div>

            <div className="mt-4 rounded-2xl border border-blue-100 bg-blue-50 p-4">
              <p className="text-xs font-semibold uppercase tracking-[0.14em] text-blue-700">내 친구 코드</p>
              <div className="mt-2 flex items-center gap-2">
                <code className="flex-1 rounded-xl bg-white px-3 py-2 font-mono text-sm font-semibold tracking-[0.08em] text-slate-900 ring-1 ring-blue-100">
                  {auth.me?.userCode ?? "-"}
                </code>
                <Button variant="secondary" size="sm" onClick={() => void copyMyCode()}>
                  복사
                </Button>
              </div>
            </div>

            <div className="mt-4 grid grid-cols-2 gap-2">
              <Link to="/friends">
                <Button className="w-full">친구</Button>
              </Link>
              <Button variant="secondary" className="w-full" onClick={() => void openCreateChatModal()}>
                대화방 만들기
              </Button>
            </div>

            {uiMessage ? (
              <p className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700">{uiMessage}</p>
            ) : null}
          </Card>
        </section>

        <section className="rounded-[30px] border border-slate-200/80 bg-white p-4 shadow-[0_20px_40px_rgba(15,23,42,0.06)]">
          <div className="mb-4 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
            <div className="min-w-0 flex-1">
              <Input
                label="검색"
                placeholder="대화방 번호, 방 이름, 최근 메시지로 검색"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
              />
            </div>

            <div className="flex flex-wrap gap-2">
              <Button onClick={() => void loadConversations()} disabled={isLoading}>
                {isLoading ? "새로고침 중..." : "새로고침"}
              </Button>
              <Button variant="secondary" onClick={() => void openCreateChatModal()}>
                대화방 만들기
              </Button>
              <Link to="/friends">
                <Button variant="secondary">친구 관리</Button>
              </Link>
            </div>
          </div>

          {error ? (
            <p className="mb-4 rounded-2xl border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{error}</p>
          ) : null}

          {filtered.length > 0 ? (
            <div className="signal-scroll grid max-h-[calc(100vh-290px)] gap-3 overflow-y-auto pr-1 md:grid-cols-2">
              {filtered.map((item) => (
                <ConversationItem key={item.conversationId} item={item} onClick={() => navigate(`/conversations/${item.conversationId}`)} />
              ))}
            </div>
          ) : (
            <Card className="rounded-3xl border-dashed p-12 text-center">
              <p className="text-base font-semibold text-slate-900">아직 대화방이 없습니다</p>
              <p className="mt-2 text-sm text-slate-500">친구를 추가하거나 대화방을 만들어 메시지를 시작해 보세요.</p>
              <div className="mt-5 flex justify-center gap-2">
                <Link to="/friends">
                  <Button>친구 추가</Button>
                </Link>
                <Button variant="secondary" onClick={() => void openCreateChatModal()}>대화방 만들기</Button>
              </div>
            </Card>
          )}
        </section>
      </div>

      <Modal
        open={groupModalOpen}
        title="대화방 만들기"
        onClose={() => {
          if (groupSubmitting) return;
          setGroupModalOpen(false);
        }}
        footer={(
          <>
            <Button variant="ghost" onClick={() => setGroupModalOpen(false)} disabled={groupSubmitting}>닫기</Button>
            <Button onClick={() => void submitGroupConversation()} disabled={groupSubmitting || groupLoading}>
              {groupSubmitting ? "생성 중..." : selectedFriendIds.length <= 1 ? "1:1 대화 시작" : "그룹 대화 만들기"}
            </Button>
          </>
        )}
      >
        <div className="space-y-3">
          <p className="text-sm text-slate-600">
            친구 1명을 선택하면 1:1 대화가 생성되고, 2명 이상을 선택하면 그룹 대화가 생성됩니다.
          </p>

          <Input
            label="그룹 방 이름 (선택)"
            placeholder="예: 프로젝트 A 스탠드업"
            value={groupRoomName}
            onChange={(e) => setGroupRoomName(e.target.value)}
            hint="친구를 2명 이상 선택해 그룹 대화를 만들 때만 저장됩니다."
            disabled={groupLoading || groupSubmitting}
          />

          {groupError ? (
            <p className="rounded-xl border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{groupError}</p>
          ) : null}

          {groupLoading ? (
            <div className="rounded-xl border border-slate-200 bg-slate-50 px-3 py-6 text-center text-sm text-slate-500">
              친구 목록을 불러오는 중...
            </div>
          ) : groupCandidates.length === 0 ? (
            <div className="rounded-xl border border-dashed border-slate-300 bg-slate-50 px-3 py-6 text-center text-sm text-slate-500">
              수락된 친구가 없습니다. 대화방을 만들려면 먼저 친구를 수락해야 합니다.
            </div>
          ) : (
            <div className="max-h-72 space-y-2 overflow-y-auto pr-1">
              {groupCandidates.map((friend) => {
                const checked = selectedFriendIds.includes(friend.friendUserId);
                return (
                  <label
                    key={friend.friendUserId}
                    className="flex cursor-pointer items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white px-3 py-2"
                  >
                    <div className="min-w-0">
                      <p className="truncate text-sm font-semibold text-slate-900">{friend.nickname}</p>
                      <p className="truncate text-xs text-slate-500">코드 {friend.userCode}</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={checked}
                      onChange={() => toggleGroupMember(friend.friendUserId)}
                      className="h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                    />
                  </label>
                );
              })}
            </div>
          )}

          <div className="text-xs text-slate-500">선택됨: {selectedFriendIds.length}명</div>
        </div>
      </Modal>
    </main>
  );
}
