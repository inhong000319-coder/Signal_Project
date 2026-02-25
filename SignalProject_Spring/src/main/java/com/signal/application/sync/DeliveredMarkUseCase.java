package com.signal.application.sync;

public interface DeliveredMarkUseCase {
    DeliveredMarkResult markDelivered(DeliveredMarkCommand command);
}
