package com.kkaebiz.api_server.jwt;

import com.kkaebiz.api_server.auth.common.AuthException;
import io.jsonwebtoken.*;
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

    public long parseAndValidateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // typ 체크(없으면 거부하는 게 안전)
            String typ = claims.get("typ", String.class);
            if (typ == null || !typ.equals("access")) {
                throw new AuthException("Invalid token type");
            }

            String sub = claims.getSubject();
            if (sub == null || sub.isBlank()) {
                throw new AuthException("Missing subject");
            }

            try {
                return Long.parseLong(sub);
            } catch (NumberFormatException e) {
                throw new AuthException("Invalid subject");
            }

        } catch (ExpiredJwtException e) {
            throw new AuthException("Access token expired", e);
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            // 서명 불일치/형식 오류/비어있음 등
            throw new AuthException("Invalid access token", e);
        }
    }
}
