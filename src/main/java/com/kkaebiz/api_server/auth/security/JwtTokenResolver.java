package com.kkaebiz.api_server.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenResolver {

    public String resolve(HttpServletRequest request) {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null) return null;
        if (!auth.startsWith("Bearer ")) return null;
        String token = auth.substring("Bearer ".length()).trim();
        return token.isEmpty() ? null : token;
    }
}
