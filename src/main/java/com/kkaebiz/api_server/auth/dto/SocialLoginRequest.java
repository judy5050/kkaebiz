package com.kkaebiz.api_server.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLoginRequest(
        @NotNull Provider provider,

        // KAKAO: access token
        // APPLE: id_token
        @NotBlank String token,

        String authorizationCode
) {}
