package com.signal.entry.sync;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.signal.application.sync.DeliveredBatchMarkCommand;
import com.signal.application.sync.DeliveredBatchMarkResult;
import com.signal.application.sync.DeliveredBatchMarkUseCase;
import com.signal.application.sync.DeliveredMarkCommand;
import com.signal.application.sync.DeliveredMarkResult;
import com.signal.application.sync.DeliveredMarkUseCase;
import com.signal.application.sync.ReadBatchMarkCommand;
import com.signal.application.sync.ReadBatchMarkResult;
import com.signal.application.sync.ReadBatchMarkUseCase;
import com.signal.application.sync.ReadMarkCommand;
import com.signal.application.sync.ReadMarkResult;
import com.signal.application.sync.ReadMarkUseCase;
import com.signal.application.sync.ReconnectQuery;
import com.signal.application.sync.ReconnectResult;
import com.signal.application.sync.ReconnectUseCase;
import com.signal.entry.sync.dto.DeliveredBatchMarkRequest;
import com.signal.entry.sync.dto.DeliveredBatchMarkResponse;
import com.signal.entry.sync.dto.DeliveredMarkRequest;
import com.signal.entry.sync.dto.DeliveredMarkResponse;
import com.signal.entry.sync.dto.ReadBatchMarkRequest;
import com.signal.entry.sync.dto.ReadBatchMarkResponse;
import com.signal.entry.sync.dto.ReadMarkRequest;
import com.signal.entry.sync.dto.ReadMarkResponse;
import com.signal.entry.sync.dto.ReconnectMessageResponse;
import com.signal.entry.sync.dto.ReconnectRequest;
import com.signal.entry.sync.dto.ReconnectResponse;
import com.signal.entry.sync.dto.ReconnectStateResponse;
import com.signal.infrastructure.security.UserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/read")
public class ReadController {
    private final ReadMarkUseCase readMarkUseCase;
    private final ReadBatchMarkUseCase readBatchMarkUseCase;
    private final ReconnectUseCase reconnectUseCase;
    private final DeliveredMarkUseCase deliveredMarkUseCase;
    private final DeliveredBatchMarkUseCase deliveredBatchMarkUseCase;

    public ReadController(
        ReadMarkUseCase readMarkUseCase,
        ReadBatchMarkUseCase readBatchMarkUseCase,
        ReconnectUseCase reconnectUseCase,
        DeliveredMarkUseCase deliveredMarkUseCase,
        DeliveredBatchMarkUseCase deliveredBatchMarkUseCase
    ) {
        this.readMarkUseCase = readMarkUseCase;
        this.readBatchMarkUseCase = readBatchMarkUseCase;
        this.reconnectUseCase = reconnectUseCase;
        this.deliveredMarkUseCase = deliveredMarkUseCase;
        this.deliveredBatchMarkUseCase = deliveredBatchMarkUseCase;
    }

    @PostMapping
    public ResponseEntity<ReadMarkResponse> markRead(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody ReadMarkRequest request
    ) {
        ReadMarkResult result = readMarkUseCase.markRead(
            new ReadMarkCommand(
                principal == null ? null : principal.getUserId(),
                request.conversationId(),
                request.messageId()
            )
        );
        return ResponseEntity.ok(new ReadMarkResponse(
            result.getConversationId(),
            result.getUserId(),
            result.getLastReadMessageId()
        ));
    }

    @PostMapping("/batch")
    public ResponseEntity<ReadBatchMarkResponse> markReadBatch(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody ReadBatchMarkRequest request
    ) {
        ReadBatchMarkResult result = readBatchMarkUseCase.markReadBatch(
            new ReadBatchMarkCommand(
                principal == null ? null : principal.getUserId(),
                request.conversationId(),
                request.messageIds()
            )
        );
        return ResponseEntity.ok(new ReadBatchMarkResponse(
            result.getConversationId(),
            result.getUserId(),
            result.getLastReadMessageId()
        ));
    }

    @PostMapping("/delivered")
    public ResponseEntity<DeliveredMarkResponse> markDelivered(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody DeliveredMarkRequest request
    ) {
        DeliveredMarkResult result = deliveredMarkUseCase.markDelivered(
            new DeliveredMarkCommand(
                principal == null ? null : principal.getUserId(),
                request.conversationId(),
                request.messageId()
            )
        );
        return ResponseEntity.ok(new DeliveredMarkResponse(
            result.getConversationId(),
            result.getUserId(),
            result.getLastDeliveredMessageId()
        ));
    }

    @PostMapping("/delivered/batch")
    public ResponseEntity<DeliveredBatchMarkResponse> markDeliveredBatch(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody DeliveredBatchMarkRequest request
    ) {
        DeliveredBatchMarkResult result = deliveredBatchMarkUseCase.markDeliveredBatch(
            new DeliveredBatchMarkCommand(
                principal == null ? null : principal.getUserId(),
                request.conversationId(),
                request.messageIds()
            )
        );
        return ResponseEntity.ok(new DeliveredBatchMarkResponse(
            result.getConversationId(),
            result.getUserId(),
            result.getLastDeliveredMessageId()
        ));
    }

    @PostMapping("/reconnect")
    public ResponseEntity<ReconnectResponse> reconnect(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody ReconnectRequest request
    ) {
        ReconnectResult result = reconnectUseCase.reconnect(new ReconnectQuery(
            principal == null ? null : principal.getUserId(),
            request.conversationId(),
            request.clientLastDeliveredMessageId(),
            request.clientLastReadMessageId(),
            request.limit() == null ? 0 : request.limit()
        ));

        List<ReconnectMessageResponse> messages = result.getMessages().stream()
            .map(m -> new ReconnectMessageResponse(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ))
            .toList();

        List<ReconnectStateResponse> states = result.getStates().stream()
            .map(s -> new ReconnectStateResponse(s.getMessageId(), s.getUserId(), s.getState()))
            .toList();

        return ResponseEntity.ok(new ReconnectResponse(
            result.getConversationId(),
            result.getUserId(),
            result.getEffectiveLastDeliveredMessageId(),
            result.getEffectiveLastReadMessageId(),
            result.getServerLastDeliveredMessageId(),
            result.getServerLastReadMessageId(),
            messages,
            states
        ));
    }
}
