package com.kkaebiz.api_server.auth.dto;

public record LoginResponse(
        String accessToken,
        long userId,
        String nickName
) {}
