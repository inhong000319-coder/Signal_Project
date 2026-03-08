import { useRef, useState } from "react";
import { Button } from "@/components/common/Button";
import { validateMessageContent } from "@/utils/validation";

interface MessageInputProps {
  onSend: (content: string) => Promise<void> | void;
  disabled?: boolean;
}

export function MessageInput({ onSend, disabled = false }: MessageInputProps) {
  const [content, setContent] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [focused, setFocused] = useState(false);
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);

  async function submit() {
    const validationError = validateMessageContent(content);
    if (validationError) {
      setError(validationError);
      return;
    }

    const next = content.trim();
    if (!next) {
      return;
    }

    setError(null);
    try {
      await onSend(next);
      setContent("");

      // WHY: send flow temporarily disables the textarea; restore focus after re-enable for fast consecutive sends.
      requestAnimationFrame(() => {
        textareaRef.current?.focus();
      });
    } catch {
      // Keep typed content for quick retry when send fails.
    }
  }

  return (
    <div>
      <form
        className={[
          "rounded-2xl border bg-white p-2 transition",
          focused ? "border-blue-400 ring-4 ring-blue-100" : error ? "border-rose-300" : "border-slate-200",
        ].join(" ")}
        onSubmit={async (event) => {
          event.preventDefault();
          if (disabled) return;
          await submit();
        }}
      >
        <textarea
          ref={textareaRef}
          value={content}
          onChange={(e) => setContent(e.target.value)}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          onKeyDown={async (e) => {
            if (e.key === "Enter" && !e.shiftKey) {
              e.preventDefault();
              if (disabled) return;
              await submit();
            }
          }}
          placeholder="메시지를 입력하세요"
          className="min-h-[56px] w-full resize-none border-0 bg-transparent px-3 py-2 text-sm text-slate-900 outline-none placeholder:text-slate-400"
          disabled={disabled}
        />

        <div className="flex items-center justify-between gap-2 border-t border-slate-100 px-1 pt-2">
          <p className="text-xs text-slate-500">엔터 전송 / Shift+엔터 줄바꿈</p>
          <Button type="submit" disabled={disabled} className="min-w-20">
            {disabled ? "전송 중" : "전송"}
          </Button>
        </div>
      </form>

      {error ? <p className="mt-2 text-xs text-rose-600">{error}</p> : null}
    </div>
  );
}
