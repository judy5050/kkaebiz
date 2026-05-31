package com.kkaebiz.api_server.config;

import com.kkaebiz.api_server.auth.apple.AppleProperties;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class AppleAuthConfig {

    @Bean
    public JWKSource<SecurityContext> appleJwkSource(AppleProperties props) throws MalformedURLException {
        URL jwksUrl = new URL(props.jwksUrl());

        return new RemoteJWKSet<>(jwksUrl);
    }
}
