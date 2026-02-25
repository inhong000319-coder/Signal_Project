package com.signal.application.sync;

public interface ReadBatchMarkUseCase {
    ReadBatchMarkResult markReadBatch(ReadBatchMarkCommand command);
}
