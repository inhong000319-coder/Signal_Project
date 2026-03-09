package com.signal.infrastructure.websocket;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import com.signal.application.message.event.MessageSentEvent;
import com.signal.application.sync.event.MessageDeliveredEvent;
import com.signal.application.sync.event.MessageReadEvent;

@Component
public class WebSocketEventHandler {
    private final WebSocketPublisher webSocketPublisher;

    public WebSocketEventHandler(WebSocketPublisher webSocketPublisher) {
        this.webSocketPublisher = webSocketPublisher;
    }

    @Async("webSocketExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageSent(MessageSentEvent event) {
        webSocketPublisher.publishMessageSent(new MessageSentPayload(
            event.getMessageId(),
            event.getConversationId(),
            event.getSenderUserId(),
            event.getContent(),
            event.getClientMessageKey(),
            event.getCreatedAt()
        ));
    }

    @Async("webSocketExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageDelivered(MessageDeliveredEvent event) {
        webSocketPublisher.publishDeliveredUpdate(new DeliveredUpdatePayload(
            event.getConversationId(),
            event.getUserId(),
            event.getMessageId(),
            event.getOccurredAt()
        ));
    }

    @Async("webSocketExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageRead(MessageReadEvent event) {
        webSocketPublisher.publishReadUpdate(new ReadUpdatePayload(
            event.getConversationId(),
            event.getUserId(),
            event.getMessageId(),
            event.getOccurredAt()
        ));
    }
}
