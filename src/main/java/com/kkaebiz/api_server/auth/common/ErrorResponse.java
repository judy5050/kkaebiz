package com.kkaebiz.api_server.auth.common;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
    public static ErrorResponse unauthorized(String message) {
        return new ErrorResponse("UNAUTHORIZED", message, Instant.now());
    }
}
