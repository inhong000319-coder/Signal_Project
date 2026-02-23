package com.signal.application.auth.port;

public interface TokenParser {
    Long parseUserId(String token);
    String parseTokenId(String token);
}
