package com.kkaebiz.api_server.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        long kakaoId
) {}
