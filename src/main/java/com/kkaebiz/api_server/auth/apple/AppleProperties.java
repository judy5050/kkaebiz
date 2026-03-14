package com.kkaebiz.api_server.auth.apple;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.apple")
public record AppleProperties(
        String issuer,
        String audience,
        String clientId,
        String teamId,
        String keyId,
        String jwksUrl,
        long jwksCacheSeconds,
        String privateKey
) {}
