package com.signal.entry.message;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.signal.application.message.MessageListQuery;
import com.signal.application.message.MessageListResult;
import com.signal.application.message.MessageListUseCase;
import com.signal.application.message.MessageSendUseCase;
import com.signal.application.message.SendMessageCommand;
import com.signal.application.message.SendMessageResult;
import com.signal.entry.message.dto.MessageListItemResponse;
import com.signal.entry.message.dto.MessageListResponse;
import com.signal.entry.message.dto.SendMessageRequest;
import com.signal.entry.message.dto.SendMessageResponse;
import com.signal.infrastructure.security.UserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageSendUseCase messageSendUseCase;
    private final MessageListUseCase messageListUseCase;

    public MessageController(MessageSendUseCase messageSendUseCase, MessageListUseCase messageListUseCase) {
        this.messageSendUseCase = messageSendUseCase;
        this.messageListUseCase = messageListUseCase;
    }

    @PostMapping
    public ResponseEntity<SendMessageResponse> send(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody SendMessageRequest request
    ) {
        SendMessageResult result = messageSendUseCase.send(
            new SendMessageCommand(
                principal == null ? null : principal.getUserId(),
                request.conversationId(),
                request.content(),
                request.clientMessageKey()
            )
        );
        return ResponseEntity.ok(new SendMessageResponse(
            result.getMessageId(),
            result.getConversationId(),
            result.getSenderUserId(),
            result.getContent(),
            result.getClientMessageKey(),
            result.getCreatedAt()
        ));
    }

    @GetMapping
    public ResponseEntity<MessageListResponse> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam Long conversationId,
        @RequestParam(required = false) Long beforeMessageId,
        @RequestParam(defaultValue = "50") int limit
    ) {
        MessageListResult result = messageListUseCase.list(
            new MessageListQuery(
                principal == null ? null : principal.getUserId(),
                conversationId,
                beforeMessageId,
                limit
            )
        );

        List<MessageListItemResponse> items = result.getMessages().stream()
            .map(m -> new MessageListItemResponse(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ))
            .toList();

        return ResponseEntity.ok(new MessageListResponse(
            items,
            result.getNextBeforeMessageId(),
            result.getLastDeliveredMessageId(),
            result.getLastReadMessageId(),
            result.getUnreadCount()
        ));
    }
}
