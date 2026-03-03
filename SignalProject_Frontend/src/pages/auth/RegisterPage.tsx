import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Card } from "@/components/common/Card";
import { Input } from "@/components/common/Input";
import { Button } from "@/components/common/Button";
import { useAuth } from "@/hooks/useAuth";
import { validateLoginId, validateNickname, validatePasswordPolicy } from "@/utils/validation";

export function RegisterPage() {
  const navigate = useNavigate();
  const auth = useAuth();
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [message, setMessage] = useState<string | null>(null);

  return (
    <main className="signal-shell-wide flex min-h-screen items-center justify-center py-8">
      <div className="grid w-full max-w-6xl gap-4 lg:grid-cols-[0.9fr_1.1fr]">
        <Card className="rounded-[30px] p-6">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">계정 만들기</p>
            <h1 className="mt-1 text-2xl font-semibold text-slate-900">회원가입</h1>
            <p className="mt-2 text-sm text-slate-500">가입이 완료되면 친구 추가에 사용할 개인 코드가 자동 발급됩니다.</p>
          </div>

          <form
            className="mt-5 space-y-4"
            onSubmit={async (e) => {
              e.preventDefault();
              const error = validateLoginId(loginId) ?? validatePasswordPolicy(password) ?? validateNickname(nickname);
              if (error) {
                setMessage(error);
                return;
              }

              try {
                const result = await auth.register(loginId, password, nickname);
                setMessage(`회원가입 완료 (친구 코드: ${result.userCode})`);
                setTimeout(() => navigate("/login"), 700);
              } catch (err) {
                setMessage((err as { message?: string }).message ?? "회원가입에 실패했어요.");
              }
            }}
          >
            <Input label="아이디" value={loginId} onChange={(e) => setLoginId(e.target.value)} />
            <Input label="비밀번호" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
            <Input label="닉네임" value={nickname} onChange={(e) => setNickname(e.target.value)} />

            {message ? <p className="rounded-2xl border border-slate-200 bg-slate-50 px-3 py-2 text-sm text-slate-700">{message}</p> : null}

            <Button type="submit" className="w-full" size="lg" disabled={auth.isLoading}>
              {auth.isLoading ? "처리 중..." : "회원가입"}
            </Button>

            <Link to="/login" className="block text-center text-sm font-medium text-blue-600 hover:text-blue-700">
              로그인으로 이동
            </Link>
          </form>
        </Card>

        <section className="relative overflow-hidden rounded-[30px] bg-gradient-to-br from-slate-900 via-slate-800 to-blue-900 p-7 text-white shadow-[0_28px_60px_rgba(15,23,42,0.28)]">
          <div className="pointer-events-none absolute -right-10 top-6 h-40 w-40 rounded-full bg-blue-300/15 blur-2xl" />
          <div className="pointer-events-none absolute bottom-4 left-6 h-28 w-28 rounded-full bg-cyan-200/15 blur-2xl" />

          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-white/65">시작 가이드</p>
          <h2 className="mt-3 text-3xl font-semibold leading-tight">가입 후 바로 확인할 수 있는 흐름</h2>
          <p className="mt-3 max-w-xl text-sm leading-6 text-white/80">
            친구 추가, 대화방 생성, 메시지 전송, 재접속 동기화까지 MVP 핵심 시나리오를 순서대로 사용할 수 있습니다.
          </p>

          <div className="mt-8 grid gap-3 sm:grid-cols-2">
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-sm font-semibold">개인 코드 발급</p>
              <p className="mt-1 text-xs text-white/75">친구 추가에 사용하는 고유 코드</p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-sm font-semibold">친구 기반 대화</p>
              <p className="mt-1 text-xs text-white/75">수락된 친구만 1:1 대화 가능</p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-sm font-semibold">실시간 알림</p>
              <p className="mt-1 text-xs text-white/75">메시지/읽음 상태 즉시 반영</p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
              <p className="text-sm font-semibold">재접속 복구</p>
              <p className="mt-1 text-xs text-white/75">동기화 누락 없이 이어짐</p>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}
