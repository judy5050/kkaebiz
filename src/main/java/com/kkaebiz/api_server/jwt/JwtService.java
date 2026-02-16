package com.kkaebiz.api_server.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpSeconds;
    private final long refreshExpSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-exp-seconds}") long accessExpSeconds,
            @Value("${app.jwt.refresh-exp-seconds}") long refreshExpSeconds
    ) {
        // HS256이면 최소 32바이트 이상 권장
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpSeconds = accessExpSeconds;
        this.refreshExpSeconds = refreshExpSeconds;
    }

    public String createAccessToken(long subjectId) {
        return createToken("access", subjectId, accessExpSeconds);
    }

    public String createRefreshToken(long subjectId) {
        return createToken("refresh", subjectId, refreshExpSeconds);
    }

    private String createToken(String typ, long subjectId, long expSeconds) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(subjectId))
                .claim("typ", typ)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expSeconds)))
                .signWith(key)
                .compact();
    }
}
