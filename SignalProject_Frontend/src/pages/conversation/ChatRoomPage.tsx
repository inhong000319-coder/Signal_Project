import { useEffect, useMemo, useState } from "react";
import { Link, Navigate, useNavigate, useParams } from "react-router-dom";
import { Button } from "@/components/common/Button";
import { Modal } from "@/components/common/Modal";
import { ConversationHeader } from "@/components/conversation/ConversationHeader";
import { ConversationItem } from "@/components/conversation/ConversationItem";
import { MessageInput } from "@/components/message/MessageInput";
import { MessageList } from "@/components/message/MessageList";
import { selectMessagePageMeta, selectMessagesByConversation } from "@/features/message/selectors";
import { useMessageStore } from "@/features/message/store";
import { selectConversationStates } from "@/features/state/selectors";
import { useMessageStateStore } from "@/features/state/store";
import { selectConnectionPhase, selectSyncCursorByConversation } from "@/features/sync/selectors";
import { useSyncStore } from "@/features/sync/store";
import { useAuthStore } from "@/features/auth/store";
import { friendshipApi } from "@/features/friendship/api";
import { useFriendshipStore } from "@/features/friendship/store";
import { useConversation } from "@/hooks/useConversation";
import { useMessageSender } from "@/hooks/useMessageSender";
import { useReconnectSync } from "@/hooks/useReconnectSync";
import { useWebSocket } from "@/hooks/useWebSocket";
import type { ConversationMemberSummary } from "@/types/domain";

function toStateLabel(state: string | null): string | null {
  if (!state) return null;
  if (state === "READ") return "읽음";
  if (state === "DELIVERED") return "전달됨";
  return "전송됨";
}

export function ChatRoomPage() {
  const navigate = useNavigate();
  const params = useParams<{ conversationId: string }>();
  const conversationId = Number(params.conversationId);
  const auth = useAuthStore();
  const me = auth.me;
  const friendship = useFriendshipStore();
  const {
    conversations,
    loadConversations,
    loadConversationMembers,
    addMembersToConversation,
    openConversation,
    loadOlderMessages,
    markDeliveredBatch,
    markReadBatch,
    leaveConversation,
    deleteConversation,
  } = useConversation();
  const reconnectSync = useReconnectSync();
  const { subscribeConversation } = useWebSocket();
  const connectionPhase = useSyncStore(selectConnectionPhase);
  const messages = useMessageStore(selectMessagesByConversation(Number.isFinite(conversationId) ? conversationId : -1));
  const pageMeta = useMessageStore(selectMessagePageMeta(Number.isFinite(conversationId) ? conversationId : -1));
  const conversationStates = useMessageStateStore(selectConversationStates(Number.isFinite(conversationId) ? conversationId : -1));
  const cursor = useSyncStore(selectSyncCursorByConversation(Number.isFinite(conversationId) ? conversationId : -1));
  const sender = useMessageSender(Number.isFinite(conversationId) ? conversationId : null);

  const [roomMembers, setRoomMembers] = useState<ConversationMemberSummary[]>([]);
  const [roomMembersLoaded, setRoomMembersLoaded] = useState(false);
  const [addMembersOpen, setAddMembersOpen] = useState(false);
  const [selectedMemberIds, setSelectedMemberIds] = useState<number[]>([]);
  const [addMembersSubmitting, setAddMembersSubmitting] = useState(false);
  const [addMembersError, setAddMembersError] = useState<string | null>(null);
  const [roomActionSubmitting, setRoomActionSubmitting] = useState<"leave" | "delete" | null>(null);

  useEffect(() => {
    if (!auth.tokenPair?.accessToken) return;
    void loadConversations().catch(() => undefined);
  }, [auth.tokenPair?.accessToken, loadConversations]);

  useEffect(() => {
    const accessToken = auth.tokenPair?.accessToken;
    if (!accessToken) return;

    let cancelled = false;
    (async () => {
      try {
        const res = await friendshipApi.list(accessToken);
        if (cancelled) return;
        useFriendshipStore.getState().setSnapshot(res);
      } catch {
        // Chat room still works without nickname enrichment.
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [auth.tokenPair?.accessToken]);

  useEffect(() => {
    if (!Number.isFinite(conversationId) || !auth.tokenPair?.accessToken) return;

    const unsubscribe = subscribeConversation(conversationId);
    let cancelled = false;

    (async () => {
      try {
        await openConversation(conversationId);
        if (cancelled) return;
        await reconnectSync(conversationId);
      } catch {
        // Keep page stable even when initial sync fails (e.g., expired session before refresh).
      }
    })();

    return () => {
      cancelled = true;
      unsubscribe();
    };
  }, [conversationId, auth.tokenPair?.accessToken, openConversation, reconnectSync, subscribeConversation]);

  useEffect(() => {
    if (!Number.isFinite(conversationId) || !auth.tokenPair?.accessToken) return;

    let cancelled = false;
    setRoomMembers([]);
    setRoomMembersLoaded(false);
    setAddMembersOpen(false);
    setSelectedMemberIds([]);
    setAddMembersError(null);

    (async () => {
      try {
        const response = await loadConversationMembers(conversationId);
        if (cancelled) return;
        setRoomMembers(response.members);
      } catch {
        if (cancelled) return;
        setRoomMembers([]);
      } finally {
        if (!cancelled) setRoomMembersLoaded(true);
      }
    })();

    return () => {
      cancelled = true;
    };
  }, [conversationId, auth.tokenPair?.accessToken, loadConversationMembers]);

  useEffect(() => {
    if (!Number.isFinite(conversationId) || !me) return;

    const incoming = messages.filter((m) => m.senderUserId !== me.userId);
    if (incoming.length === 0) return;

    const deliveredBase = cursor?.lastDeliveredMessageId ?? 0;
    const readBase = cursor?.lastReadMessageId ?? 0;
    const toDeliver = incoming.filter((m) => m.messageId > deliveredBase).map((m) => m.messageId);
    const toRead = incoming.filter((m) => m.messageId > readBase).map((m) => m.messageId);

    if (toDeliver.length > 0) {
      void markDeliveredBatch(conversationId, toDeliver).catch(() => undefined);
    }
    if (toRead.length > 0) {
      void markReadBatch(conversationId, toRead).catch(() => undefined);
    }
  }, [conversationId, me, messages, cursor?.lastDeliveredMessageId, cursor?.lastReadMessageId, markDeliveredBatch, markReadBatch]);

  const activeConversation = useMemo(
    () => conversations.find((item) => item.conversationId === conversationId) ?? null,
    [conversations, conversationId]
  );

  const senderNameByUserId = useMemo(() => {
    const map: Record<number, string> = {};
    if (me) map[me.userId] = me.nickname;
    roomMembers.forEach((member) => {
      map[member.userId] = member.nickname;
    });
    friendship.friends.forEach((friend) => {
      map[friend.friendUserId] = friend.nickname;
    });
    return map;
  }, [friendship.friends, me, roomMembers]);

  const directFriend = useMemo(
    () => friendship.friends.find((friend) => friend.conversationId === conversationId) ?? null,
    [friendship.friends, conversationId]
  );

  const observedMemberCount = useMemo(() => {
    const ids = new Set<number>();
    if (me) ids.add(me.userId);
    messages.forEach((message) => ids.add(message.senderUserId));
    Object.values(conversationStates).forEach((state) => ids.add(state.userId));
    return ids.size > 0 ? ids.size : null;
  }, [me, messages, conversationStates]);

  const memberCount = useMemo(() => {
    if (roomMembersLoaded) return roomMembers.length;
    if (activeConversation?.type === "DIRECT") return 2;
    return observedMemberCount;
  }, [activeConversation?.type, observedMemberCount, roomMembers.length, roomMembersLoaded]);

  const myRoleInRoom = useMemo(() => {
    if (!me) return null;
    return roomMembers.find((member) => member.userId === me.userId)?.role ?? null;
  }, [me, roomMembers]);

  const canLeaveCurrentConversation = activeConversation?.type === "GROUP" && myRoleInRoom === "MEMBER";

  const canDeleteCurrentConversation =
    activeConversation?.type === "DIRECT"
      ? true
      : activeConversation?.type === "GROUP" && myRoleInRoom === "OWNER";

  const headerTitle = useMemo(() => {
    if (activeConversation?.type === "DIRECT") {
      if (directFriend?.nickname) return directFriend.nickname;
      const firstOtherMessage = messages.find((message) => message.senderUserId !== me?.userId);
      if (firstOtherMessage) return senderNameByUserId[firstOtherMessage.senderUserId] ?? `사용자 ${firstOtherMessage.senderUserId}`;
      return "1:1 대화";
    }
    if (activeConversation?.type === "GROUP") {
      return activeConversation.roomName?.trim() || `그룹 대화 #${conversationId}`;
    }
    return conversationId ? `대화방 #${conversationId}` : "채팅";
  }, [activeConversation, conversationId, directFriend?.nickname, me?.userId, messages, senderNameByUserId]);

  const groupRecipientCountForLabels = useMemo(() => {
    const totalMembers = roomMembersLoaded ? roomMembers.length : (memberCount ?? 0);
    return Math.max(totalMembers - 1, 0);
  }, [memberCount, roomMembers.length, roomMembersLoaded]);

  const stateLabelByMessageId = useMemo(() => {
    const labels: Record<number, string | null> = {};
    const isGroup = activeConversation?.type === "GROUP";

    for (const message of messages) {
      const rows = Object.values(conversationStates).filter((state) => state.messageId === message.messageId);

      if (isGroup) {
        const readCount = rows.filter((s) => s.userId !== message.senderUserId && s.state === "READ").length;
        const unreadRemaining = Math.max(groupRecipientCountForLabels - readCount, 0);
        labels[message.messageId] = `안 읽음 ${unreadRemaining}`;
        continue;
      }

      const highest = rows.some((s) => s.state === "READ")
        ? "READ"
        : rows.some((s) => s.state === "DELIVERED")
          ? "DELIVERED"
          : rows.some((s) => s.state === "SENT")
            ? "SENT"
            : null;
      labels[message.messageId] = toStateLabel(highest);
    }
    return labels;
  }, [messages, conversationStates, activeConversation?.type, groupRecipientCountForLabels]);

  const addableFriends = useMemo(() => {
    const existing = new Set<number>(roomMembers.map((member) => member.userId));
    if (me) existing.add(me.userId);
    return friendship.friends.filter((friend) => !existing.has(friend.friendUserId));
  }, [friendship.friends, me, roomMembers]);

  function toggleAdditionalMember(userId: number) {
    setSelectedMemberIds((prev) => (prev.includes(userId) ? prev.filter((id) => id !== userId) : [...prev, userId]));
  }

  function openAddMembersModal() {
    setAddMembersError(null);
    setSelectedMemberIds([]);
    setAddMembersOpen(true);
  }

  async function submitAddMembers() {
    if (!Number.isFinite(conversationId)) return;
    if (selectedMemberIds.length === 0) {
      setAddMembersError("친구를 1명 이상 선택해 주세요.");
      return;
    }

    setAddMembersSubmitting(true);
    setAddMembersError(null);
    try {
      const response = await addMembersToConversation(conversationId, selectedMemberIds);
      setRoomMembers(response.members);
      setRoomMembersLoaded(true);
      setAddMembersOpen(false);
      setSelectedMemberIds([]);
    } catch (error) {
      setAddMembersError((error as { message?: string }).message ?? "참여자 추가에 실패했습니다.");
    } finally {
      setAddMembersSubmitting(false);
    }
  }


  async function leaveCurrentConversation() {
    if (!Number.isFinite(conversationId)) return;
    if (typeof window !== "undefined") {
      const ok = window.confirm("정말 이 그룹 대화방을 나가시겠습니까?");
      if (!ok) return;
    }

    setRoomActionSubmitting("leave");
    try {
      await leaveConversation(conversationId);
      navigate("/conversations", { replace: true });
    } catch (error) {
      const message = (error as { message?: string }).message ?? "대화방 나가기에 실패했습니다.";
      if (typeof window !== "undefined") {
        window.alert(message);
      }
    } finally {
      setRoomActionSubmitting(null);
    }
  }

  async function deleteCurrentConversation() {
    if (!Number.isFinite(conversationId)) return;
    if (typeof window !== "undefined") {
      const ok = window.confirm("정말 이 대화방을 삭제하시겠습니까?");
      if (!ok) return;
    }

    setRoomActionSubmitting("delete");
    try {
      await deleteConversation(conversationId);
      navigate("/conversations", { replace: true });
    } catch (error) {
      const message = (error as { message?: string }).message ?? "대화방 삭제에 실패했습니다.";
      if (typeof window !== "undefined") {
        window.alert(message);
      }
    } finally {
      setRoomActionSubmitting(null);
    }
  }

  if (!auth.tokenPair?.accessToken) return <Navigate to="/login" replace />;
  if (!Number.isFinite(conversationId)) return <Navigate to="/error" replace />;

  return (
    <>
      <main className="min-h-screen bg-[#f4f5f7] text-slate-900">
        <div className="mx-auto max-w-[1480px] px-4 pb-6 pt-5 sm:px-6 lg:px-8">
          <header className="mb-4 flex items-center justify-between gap-4">
            <Link to="/conversations" className="inline-flex items-center gap-3">
              <span className="grid h-11 w-11 place-items-center rounded-2xl bg-gradient-to-br from-blue-500 to-purple-600 text-white shadow-[0_8px_18px_rgba(59,130,246,0.28)]">
                <svg viewBox="0 0 24 24" className="h-5 w-5" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden>
                  <path d="M21 11.5a8.5 8.5 0 0 1-8.5 8.5c-1.5 0-2.92-.4-4.15-1.1L3 20.5l1.67-4.93A8.5 8.5 0 1 1 21 11.5Z" />
                </svg>
              </span>
              <div>
                <p className="text-lg font-semibold tracking-tight text-slate-900">Signal</p>
                <p className="text-xs text-slate-500">채팅</p>
              </div>
            </Link>

            <div className="flex items-center gap-2">
              <Link to="/friends"><Button variant="secondary">친구</Button></Link>
              <Link to="/conversations"><Button>대화 목록</Button></Link>
            </div>
          </header>

          <div className="overflow-hidden rounded-[28px] border border-slate-200/80 bg-white shadow-[0_18px_36px_rgba(15,23,42,0.06)]">
            <div className="grid min-h-[calc(100vh-120px)] lg:grid-cols-[320px_minmax(0,1fr)]">
              <aside className="border-b border-slate-200 bg-white lg:border-b-0 lg:border-r">
                <div className="border-b border-slate-100 px-4 py-4">
                  <p className="text-sm font-semibold text-slate-900">{auth.me?.nickname ?? auth.me?.loginId}</p>
                  <p className="mt-1 text-xs text-slate-500">친구 코드 {auth.me?.userCode ?? "-"}</p>

                  <div className="mt-3 flex gap-2">
                    <Button size="sm" variant="secondary" className="flex-1" onClick={() => void loadConversations()}>새로고침</Button>
                    <Link to="/friends" className="flex-1"><Button size="sm" variant="ghost" className="w-full">친구</Button></Link>
                  </div>
                </div>

                <div className="px-3 py-3">
                  <div className="mb-2 flex items-center justify-between px-1 text-xs text-slate-500">
                    <span className="font-semibold uppercase tracking-[0.14em]">대화방</span>
                    <span>{conversations.length}</span>
                  </div>

                  <div className="signal-scroll max-h-[calc(100vh-290px)] space-y-1 overflow-y-auto pr-1">
                    {conversations.map((item) => (
                      <ConversationItem
                        key={item.conversationId}
                        item={item}
                        active={item.conversationId === conversationId}
                        onClick={() => navigate(`/conversations/${item.conversationId}`)}
                      />
                    ))}

                    {conversations.length === 0 ? (
                      <div className="rounded-2xl border border-dashed border-slate-300 bg-slate-50 px-4 py-6 text-sm text-slate-500">
                        아직 대화방이 없습니다. 친구를 추가하거나 대화방을 만들어 보세요.
                      </div>
                    ) : null}
                  </div>
                </div>
              </aside>

              <section className="min-w-0 bg-white">
                <div className="flex h-full min-h-[calc(100vh-120px)] flex-col">
                  <ConversationHeader
                    conversationId={conversationId}
                    connectionPhase={connectionPhase}
                    title={headerTitle}
                    memberCount={memberCount}
                  />

                  <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-100 bg-slate-50/70 px-5 py-3 text-xs">
                    <div className="flex flex-wrap items-center gap-2">
                      {pageMeta.unreadCount > 0 ? (
                        <span className="rounded-full bg-blue-50 px-3 py-1 font-semibold text-blue-700">안 읽음 {pageMeta.unreadCount}</span>
                      ) : (
                        <span className="rounded-full bg-white px-3 py-1 text-slate-600 ring-1 ring-slate-200">모두 읽음</span>
                      )}
                      <span className="rounded-full bg-white px-3 py-1 text-slate-500 ring-1 ring-slate-200">서버 기반 전달/읽음 상태</span>
                      {activeConversation?.type === "GROUP" && roomMembersLoaded ? (
                        <span className="rounded-full bg-white px-3 py-1 text-slate-500 ring-1 ring-slate-200">참여자 {roomMembers.length}명</span>
                      ) : null}
                    </div>

                    <div className="flex flex-wrap items-center gap-2">
                                            {canLeaveCurrentConversation ? (
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => void leaveCurrentConversation()}
                          disabled={roomActionSubmitting !== null}
                        >
                          {roomActionSubmitting === "leave" ? "나가는 중..." : "대화방 나가기"}
                        </Button>
                      ) : null}
                      {canDeleteCurrentConversation ? (
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => void deleteCurrentConversation()}
                          disabled={roomActionSubmitting !== null}
                        >
                          {roomActionSubmitting === "delete" ? "삭제 중..." : "대화방 삭제"}
                        </Button>
                      ) : null}
                      {activeConversation?.type === "GROUP" ? (
                        <Button
                          variant="secondary"
                          size="sm"
                          onClick={openAddMembersModal}
                          disabled={addMembersSubmitting || addableFriends.length === 0}
                        >
                          {addableFriends.length === 0 ? "추가할 친구 없음" : "친구 추가"}
                        </Button>
                      ) : null}
                      <Button
                        variant="secondary"
                        size="sm"
                        onClick={() => void loadOlderMessages(conversationId)}
                        disabled={!pageMeta.nextBeforeMessageId || pageMeta.isLoading}
                      >
                        {pageMeta.isLoading ? "불러오는 중..." : "이전 메시지"}
                      </Button>
                    </div>
                  </div>

                  <div className="min-h-0 flex-1 bg-[#f8faff] px-4 py-4">
                    <div className="h-full overflow-hidden rounded-2xl border border-slate-200 bg-white">
                      <MessageList
                        messages={messages}
                        myUserId={me?.userId ?? null}
                        stateLabelByMessageId={stateLabelByMessageId}
                        senderNameByUserId={senderNameByUserId}
                      />
                    </div>
                  </div>

                  <div className="border-t border-slate-100 bg-white px-4 py-4">
                    {sender.error ? (
                      <p className="mb-3 rounded-2xl border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{sender.error}</p>
                    ) : null}
                    <MessageInput disabled={sender.isSending} onSend={async (content) => { await sender.send(content); }} />
                  </div>
                </div>
              </section>
            </div>
          </div>
        </div>
      </main>

      <Modal
        open={addMembersOpen}
        title="그룹 대화에 친구 추가"
        onClose={() => {
          if (addMembersSubmitting) return;
          setAddMembersOpen(false);
        }}
        footer={(
          <>
            <Button variant="ghost" onClick={() => setAddMembersOpen(false)} disabled={addMembersSubmitting}>닫기</Button>
            <Button onClick={() => void submitAddMembers()} disabled={addMembersSubmitting || selectedMemberIds.length === 0}>
              {addMembersSubmitting ? "추가 중..." : "참여자 추가"}
            </Button>
          </>
        )}
      >
        <div className="space-y-3">
          <p className="text-sm text-slate-600">수락된 친구를 선택해 이 그룹 대화에 추가하세요.</p>

          {addMembersError ? (
            <p className="rounded-xl border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{addMembersError}</p>
          ) : null}

          {addableFriends.length === 0 ? (
            <div className="rounded-xl border border-dashed border-slate-300 bg-slate-50 px-3 py-6 text-center text-sm text-slate-500">
              추가할 수 있는 수락된 친구가 없습니다.
            </div>
          ) : (
            <div className="max-h-72 space-y-2 overflow-y-auto pr-1">
              {addableFriends.map((friend) => {
                const checked = selectedMemberIds.includes(friend.friendUserId);
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
                      onChange={() => toggleAdditionalMember(friend.friendUserId)}
                      className="h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500"
                    />
                  </label>
                );
              })}
            </div>
          )}

          <div className="text-xs text-slate-500">선택됨: {selectedMemberIds.length}명</div>
        </div>
      </Modal>
    </>
  );
}

