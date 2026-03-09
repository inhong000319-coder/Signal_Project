package com.signal.entry.conversation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.signal.application.conversation.ConversationListQuery;
import com.signal.application.conversation.ConversationListResult;
import com.signal.application.conversation.ConversationListUseCase;
import com.signal.application.conversation.ConversationResult;
import com.signal.application.conversation.ConversationUseCase;
import com.signal.application.conversation.CreateDirectConversationCommand;
import com.signal.entry.conversation.dto.ConversationListResponse;
import com.signal.entry.conversation.dto.ConversationResponse;
import com.signal.entry.conversation.dto.ConversationSummaryResponse;
import com.signal.entry.conversation.dto.CreateDirectConversationRequest;
import com.signal.infrastructure.security.UserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationUseCase conversationUseCase;
    private final ConversationListUseCase conversationListUseCase;

    public ConversationController(ConversationUseCase conversationUseCase, ConversationListUseCase conversationListUseCase) {
        this.conversationUseCase = conversationUseCase;
        this.conversationListUseCase = conversationListUseCase;
    }

    @PostMapping("/direct")
    public ResponseEntity<ConversationResponse> createDirect(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody CreateDirectConversationRequest request
    ) {
        ConversationResult result = conversationUseCase.createDirect(
            new CreateDirectConversationCommand(principal == null ? null : principal.getUserId(), request.targetUserId())
        );
        return ResponseEntity.ok(new ConversationResponse(
            result.getConversationId(),
            result.getType(),
            result.isActive(),
            result.getCreatedAt()
        ));
    }

    @GetMapping
    public ResponseEntity<ConversationListResponse> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(defaultValue = "50") int limit
    ) {
        ConversationListResult result = conversationListUseCase.list(
            new ConversationListQuery(principal == null ? null : principal.getUserId(), limit)
        );

        List<ConversationSummaryResponse> items = result.getConversations().stream()
            .map(c -> new ConversationSummaryResponse(
                c.getConversationId(),
                c.getType(),
                c.isActive(),
                c.getLastMessageId(),
                c.getLastMessageContent(),
                c.getLastMessageSenderUserId(),
                c.getLastMessageCreatedAt(),
                c.getUnreadCount()
            ))
            .toList();

        return ResponseEntity.ok(new ConversationListResponse(items));
    }
}
