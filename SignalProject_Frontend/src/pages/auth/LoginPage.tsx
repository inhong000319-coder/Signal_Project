import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Card } from "@/components/common/Card";
import { Input } from "@/components/common/Input";
import { Button } from "@/components/common/Button";
import { useAuth } from "@/hooks/useAuth";
import { validateLoginId, validatePasswordForLogin } from "@/utils/validation";

export function LoginPage() {
  const navigate = useNavigate();
  const auth = useAuth();
  const [loginId, setLoginId] = useState("");
  const [password, setPassword] = useState("");
  const [localError, setLocalError] = useState<string | null>(null);

  return (
    <main className="signal-shell-wide flex min-h-screen items-center justify-center py-8">
      <div className="grid w-full max-w-6xl gap-4 lg:grid-cols-[1.15fr_0.85fr]">
        <section className="relative overflow-hidden rounded-[30px] bg-gradient-to-br from-blue-700 via-blue-600 to-cyan-500 p-7 text-white shadow-[0_28px_60px_rgba(37,99,235,0.24)]">
          <div className="pointer-events-none absolute -right-8 top-8 h-32 w-32 rounded-full bg-white/20 blur-2xl" />
          <div className="pointer-events-none absolute bottom-6 left-8 h-24 w-24 rounded-full bg-cyan-100/25 blur-2xl" />

          <p className="text-xs font-semibold uppercase tracking-[0.2em] text-white/75">Signal</p>
          <h1 className="mt-3 text-3xl font-semibold leading-tight">실시간 메시징을 시작하세요</h1>
          <p className="mt-3 max-w-xl text-sm leading-6 text-white/90">
            로그인 후 친구 추가, 대화 목록, 채팅, 재접속 동기화까지 바로 사용할 수 있습니다.
          </p>

          <div className="mt-8 grid gap-3 sm:grid-cols-3">
            <div className="rounded-2xl border border-white/20 bg-white/10 p-4 backdrop-blur">
              <p className="text-xs text-white/70">1</p>
              <p className="mt-1 text-sm font-semibold">로그인</p>
              <p className="mt-1 text-xs text-white/80">계정으로 접속</p>
            </div>
            <div className="rounded-2xl border border-white/20 bg-white/10 p-4 backdrop-blur">
              <p className="text-xs text-white/70">2</p>
              <p className="mt-1 text-sm font-semibold">친구 추가</p>
              <p className="mt-1 text-xs text-white/80">친구 코드 공유</p>
            </div>
            <div className="rounded-2xl border border-white/20 bg-white/10 p-4 backdrop-blur">
              <p className="text-xs text-white/70">3</p>
              <p className="mt-1 text-sm font-semibold">대화 시작</p>
              <p className="mt-1 text-xs text-white/80">실시간 상태 반영</p>
            </div>
          </div>
        </section>

        <Card className="rounded-[30px] p-6">
          <form
            className="space-y-4"
            onSubmit={async (e) => {
              e.preventDefault();
              const validationError = validateLoginId(loginId) ?? validatePasswordForLogin(password);
              if (validationError) {
                setLocalError(validationError);
                return;
              }
              setLocalError(null);
              try {
                await auth.login(loginId, password);
                navigate("/friends");
              } catch {
                // store error handled in auth store
              }
            }}
          >
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-400">인증</p>
              <h2 className="mt-1 text-2xl font-semibold text-slate-900">로그인</h2>
              <p className="mt-1 text-sm text-slate-500">아이디와 비밀번호를 입력해 주세요.</p>
            </div>

            <Input label="아이디" value={loginId} onChange={(e) => setLoginId(e.target.value)} />
            <Input label="비밀번호" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />

            {(localError || auth.error) ? (
              <p className="rounded-2xl border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-700">{localError ?? auth.error}</p>
            ) : null}

            <Button type="submit" className="w-full" size="lg" disabled={auth.isLoading}>
              {auth.isLoading ? "로그인 중..." : "로그인"}
            </Button>

            <div className="flex items-center justify-between gap-2 text-sm">
              <Link to="/register" className="font-medium text-blue-600 hover:text-blue-700">회원가입</Link>
              <Link to="/" className="text-slate-500 hover:text-slate-700">랜딩 페이지</Link>
            </div>
          </form>
        </Card>
      </div>
    </main>
  );
}
