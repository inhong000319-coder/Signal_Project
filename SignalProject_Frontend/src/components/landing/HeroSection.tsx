import { Link } from "react-router-dom";
import { Button } from "@/components/common/Button";

export function HeroSection() {
  return (
    <section className="grid gap-6 rounded-2xl border border-signal-line bg-white p-6 shadow-panel md:grid-cols-[1.2fr_0.8fr]">
      <div>
        <p className="text-xs font-semibold uppercase tracking-[0.18em] text-signal-accent">Signal 백엔드 신뢰성 데모</p>
        <h1 className="mt-3 text-3xl font-bold leading-tight text-signal-ink md:text-4xl">
          실시간 채팅 UI가 아니라,
          <br />
          신뢰성 설계를 소비하는 프론트
        </h1>
        <p className="mt-4 max-w-xl text-sm leading-6 text-signal-muted">
          Message / State / Sync를 분리하고, REST command + STOMP push-only 모델을 그대로 반영한 프론트 MVP입니다.
        </p>
        <div className="mt-5 flex flex-wrap gap-2">
          <Link to="/login"><Button>로그인으로 시작</Button></Link>
          <Link to="/register"><Button variant="secondary">회원가입</Button></Link>
        </div>
      </div>
      <div className="rounded-xl border border-dashed border-signal-line bg-gradient-to-br from-blue-50 to-orange-50 p-4 text-xs leading-6 text-signal-ink">
        <p className="font-semibold">핵심 원칙</p>
        <ul className="mt-2 space-y-1">
          <li>REST = 명령</li>
          <li>WS(STOMP) = 커밋 후 이벤트 푸시</li>
          <li>메시지/상태/동기화 분리</li>
          <li>클라이언트 메시지 키 사용</li>
          <li>재접속 동기화 필수</li>
        </ul>
      </div>
    </section>
  );
}
