import { Link } from "react-router-dom";
import { Card } from "@/components/common/Card";
import { Button } from "@/components/common/Button";

export function ErrorPage() {
  return (
    <main className="signal-shell flex min-h-screen items-center justify-center">
      <Card>
        <div className="w-[320px] text-center">
          <h1 className="text-xl font-semibold">페이지를 찾을 수 없습니다.</h1>
          <p className="mt-2 text-sm text-signal-muted">라우트 경로를 확인하세요.</p>
          <div className="mt-4 flex justify-center gap-2">
            <Link to="/"><Button>홈</Button></Link>
            <Link to="/login"><Button variant="secondary">로그인</Button></Link>
          </div>
        </div>
      </Card>
    </main>
  );
}
