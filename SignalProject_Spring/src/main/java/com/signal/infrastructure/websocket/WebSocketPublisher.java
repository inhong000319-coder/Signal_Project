package com.signal.infrastructure.websocket;

public interface WebSocketPublisher {
    void publishMessageSent(MessageSentPayload payload);
    void publishReadUpdate(ReadUpdatePayload payload);
    void publishDeliveredUpdate(DeliveredUpdatePayload payload);
}
