package com.kkaebiz.api_server.auth.security;

import com.kkaebiz.api_server.auth.common.AuthException;
import com.kkaebiz.api_server.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenResolver resolver;
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtTokenResolver resolver, JwtService jwtService) {
        this.resolver = resolver;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolver.resolve(request);

        if (token != null) {
            try {
                long userId = jwtService.parseAndValidateAccessToken(token);

                var auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (AuthException e) {
                SecurityContextHolder.clearContext();
                throw e; // ControllerAdvice가 401로 통일
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                throw new AuthException("Invalid access token", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
