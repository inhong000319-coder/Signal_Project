import type { Message } from "@/types/domain";
import { MessageItem } from "@/components/message/MessageItem";

interface MessageListProps {
  messages: Message[];
  myUserId: number | null;
  stateLabelByMessageId?: Record<number, string | null>;
  senderNameByUserId?: Record<number, string>;
}

export function MessageList({ messages, myUserId, stateLabelByMessageId = {}, senderNameByUserId = {} }: MessageListProps) {
  return (
    <div className="signal-scroll h-full min-h-[360px] overflow-y-auto bg-[#fbfcff] px-4 py-5">
      <ul className="flex min-h-full flex-col gap-4">
        {messages.map((message) => (
          <MessageItem
            key={message.messageId}
            message={message}
            isMine={myUserId === message.senderUserId}
            stateLabel={stateLabelByMessageId[message.messageId] ?? null}
            senderName={senderNameByUserId[message.senderUserId] ?? null}
          />
        ))}

        {messages.length === 0 ? (
          <li className="my-auto rounded-2xl border border-dashed border-slate-300 bg-white px-6 py-8 text-center text-sm text-slate-500">
            아직 메시지가 없습니다. 첫 메시지를 보내 대화를 시작해 보세요.
          </li>
        ) : null}
      </ul>
    </div>
  );
}