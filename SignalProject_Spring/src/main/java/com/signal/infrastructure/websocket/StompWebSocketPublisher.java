package com.signal.infrastructure.websocket;

import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Primary
public class StompWebSocketPublisher implements WebSocketPublisher {
    private final SimpMessagingTemplate messagingTemplate;

    public StompWebSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void publishMessageSent(MessageSentPayload payload) {
        messagingTemplate.convertAndSend(topic(payload.conversationId(), "messages"), payload);
    }

    @Override
    public void publishReadUpdate(ReadUpdatePayload payload) {
        messagingTemplate.convertAndSend(topic(payload.conversationId(), "reads"), payload);
    }

    @Override
    public void publishDeliveredUpdate(DeliveredUpdatePayload payload) {
        messagingTemplate.convertAndSend(topic(payload.conversationId(), "delivered"), payload);
    }

    private String topic(Long conversationId, String suffix) {
        return "/topic/conversations/" + conversationId + "/" + suffix;
    }
}
