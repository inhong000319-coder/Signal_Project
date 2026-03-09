package com.signal.infrastructure.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingWebSocketPublisher implements WebSocketPublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingWebSocketPublisher.class);

    @Override
    public void publishMessageSent(MessageSentPayload payload) {
        log.info("[WS] message sent: conversationId={}, messageId={}, senderUserId={}",
            payload.conversationId(), payload.messageId(), payload.senderUserId());
    }

    @Override
    public void publishReadUpdate(ReadUpdatePayload payload) {
        log.info("[WS] read update: conversationId={}, messageId={}, userId={}",
            payload.conversationId(), payload.messageId(), payload.userId());
    }

    @Override
    public void publishDeliveredUpdate(DeliveredUpdatePayload payload) {
        log.info("[WS] delivered update: conversationId={}, messageId={}, userId={}",
            payload.conversationId(), payload.messageId(), payload.userId());
    }
}
