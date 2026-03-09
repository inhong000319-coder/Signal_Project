package com.signal.infrastructure.user;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.signal.application.user.port.UserCodeGenerator;

@Component
public class RandomUserCodeGenerator implements UserCodeGenerator {
    private static final String ALLOWED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LEN = 6;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        // UserCode must be unpredictable and limited to the allowed character set.
        StringBuilder sb = new StringBuilder(CODE_LEN);
        for (int i = 0; i < CODE_LEN; i++) {
            int idx = random.nextInt(ALLOWED.length());
            sb.append(ALLOWED.charAt(idx));
        }
        return sb.toString();
    }
}
