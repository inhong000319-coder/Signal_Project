import { Link } from "react-router-dom";
import { Button } from "@/components/common/Button";

export function CTASection() {
  return (
    <section className="rounded-2xl border border-signal-line bg-signal-ink p-6 text-white shadow-panel">
      <p className="text-xs uppercase tracking-[0.16em] text-white/70">MVP 흐름</p>
      <h2 className="mt-2 text-2xl font-semibold">인증 → 친구 → 대화 목록 → 채팅 → 재접속 동기화</h2>
      <div className="mt-4 flex gap-2">
        <Link to="/friends"><Button className="bg-white text-signal-ink hover:bg-white/90">친구 화면</Button></Link>
        <Link to="/conversations"><Button variant="ghost" className="border border-white/30 text-white hover:bg-white/10">대화방 목록</Button></Link>
      </div>
    </section>
  );
}
