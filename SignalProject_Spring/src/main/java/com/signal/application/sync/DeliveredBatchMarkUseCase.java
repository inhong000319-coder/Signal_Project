package com.signal.application.sync;

public interface DeliveredBatchMarkUseCase {
    DeliveredBatchMarkResult markDeliveredBatch(DeliveredBatchMarkCommand command);
}
