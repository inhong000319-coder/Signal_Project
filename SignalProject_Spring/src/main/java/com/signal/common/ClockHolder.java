package com.signal.common;

import java.time.Instant;

public interface ClockHolder {
    Instant now();
}
