package com.kkaebiz.api_server.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest(
        @NotBlank String kakaoAccessToken
) {}
