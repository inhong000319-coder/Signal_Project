import { Link } from "react-router-dom";
import { Button } from "@/components/common/Button";

export function LandingPage() {
  return (
    <main className="min-h-screen bg-[#f4f5f7] text-slate-900">
      <div className="mx-auto max-w-7xl px-4 pb-16 pt-6 sm:px-6 lg:px-8">
        <header className="flex items-center justify-between gap-4 py-2">
          <Link to="/" className="inline-flex items-center gap-3">
            <span className="relative grid h-12 w-12 place-items-center rounded-2xl bg-gradient-to-br from-blue-500 to-purple-600 text-white shadow-[0_8px_18px_rgba(59,130,246,0.28)]">
              <svg
                viewBox="0 0 24 24"
                className="h-6 w-6"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                aria-hidden
              >
                <path d="M21 11.5a8.5 8.5 0 0 1-8.5 8.5c-1.5 0-2.92-.4-4.15-1.1L3 20.5l1.67-4.93A8.5 8.5 0 1 1 21 11.5Z" />
              </svg>
            </span>
            <span className="bg-gradient-to-r from-blue-600 via-indigo-500 to-purple-600 bg-clip-text text-3xl font-semibold tracking-tight text-transparent">
              Signal
            </span>
          </Link>

          <nav className="flex items-center gap-3">
            <Link to="/login" className="px-3 py-2 text-sm font-medium text-slate-700 hover:text-slate-900">
              로그인
            </Link>
            <Link to="/login">
              <Button className="h-10 rounded-xl px-5">시작하기</Button>
            </Link>
          </nav>
        </header>

        <div className="mt-5 border-t border-slate-200/90" />

        <section className="flex min-h-[calc(100vh-180px)] flex-col items-center justify-center text-center">
          <div className="inline-flex items-center gap-2 rounded-full border border-blue-200 bg-blue-50 px-5 py-2 text-sm font-medium text-blue-700 shadow-sm">
            <span aria-hidden>✨</span>
            <span>실시간 메시징의 새로운 기준</span>
          </div>

          <h1 className="mt-8 text-5xl font-semibold leading-tight tracking-tight text-slate-900 sm:text-6xl md:text-7xl">
            빠르고 안전한
            <br />
            <span className="bg-gradient-to-r from-blue-600 via-indigo-500 to-purple-600 bg-clip-text text-transparent">
              실시간 소통
            </span>
          </h1>

          <p className="mt-10 max-w-3xl text-lg leading-9 text-slate-600 sm:text-xl">
            Signal과 함께 언제 어디서나 팀원들과 즉시 연결되세요.
            <br />
            빠른 메시지 전송과 안정적인 동기화로 업무 효율을 높이세요.
          </p>

          <div className="mt-12 flex items-center justify-center">
            <Link to="/login">
              <Button size="lg" className="h-14 rounded-2xl px-10 text-base">
                시작하기
              </Button>
            </Link>
          </div>
        </section>
      </div>
    </main>
  );
}