package com.kkaebiz.api_server.auth.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> auth(AuthException e) {
        return ResponseEntity.status(401).body(ErrorResponse.unauthorized(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> badArg(IllegalArgumentException e) {
        // 요청값 문제도 정책상 401로 통일하고 싶다 했으니 401로 보냄
        return ResponseEntity.status(401).body(ErrorResponse.unauthorized(e.getMessage()));
    }
}
