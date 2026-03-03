export function FeatureSection() {
  const items = [
    ["인증", "액세스/리프레시 JWT 기반 세션 흐름"],
    ["친구 관계", "친구 수락 전제 1:1 대화방 생성"],
    ["메시징", "멱등성 키 기반 메시지 전송"],
    ["동기화", "동기화 커서 기반 재접속 복구"],
  ];

  return (
    <section className="grid gap-3 md:grid-cols-2">
      {items.map(([title, desc]) => (
        <article key={title} className="rounded-xl border border-signal-line bg-white p-4 shadow-panel">
          <h3 className="text-sm font-semibold">{title}</h3>
          <p className="mt-1 text-sm text-signal-muted">{desc}</p>
        </article>
      ))}
    </section>
  );
}
