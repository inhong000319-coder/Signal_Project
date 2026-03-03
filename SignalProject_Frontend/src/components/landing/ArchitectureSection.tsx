export function ArchitectureSection() {
  return (
    <section className="rounded-2xl border border-signal-line bg-white p-5 shadow-panel">
      <h2 className="text-lg font-semibold">백엔드 아키텍처를 반영한 프론트 구조</h2>
      <div className="mt-4 grid gap-3 text-sm md:grid-cols-3">
        <div className="rounded-lg border border-signal-line p-3">
          <p className="font-semibold">메시지 스토어</p>
          <p className="mt-1 text-signal-muted">추가 전용 메시지 목록. 상태 필드 없음.</p>
        </div>
        <div className="rounded-lg border border-signal-line p-3">
          <p className="font-semibold">상태 스토어</p>
          <p className="mt-1 text-signal-muted">읽음/전달됨 상태 업데이트 별도 저장.</p>
        </div>
        <div className="rounded-lg border border-signal-line p-3">
          <p className="font-semibold">동기화 스토어</p>
          <p className="mt-1 text-signal-muted">서버 동기화 커서를 기준으로 반영.</p>
        </div>
      </div>
    </section>
  );
}
