package com.signal.common;

import java.time.Instant;

import org.springframework.stereotype.Component;

@Component
public class SystemClockHolder implements ClockHolder {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
