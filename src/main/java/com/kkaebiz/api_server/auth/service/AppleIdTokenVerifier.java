package com.kkaebiz.api_server.auth.service;

import com.kkaebiz.api_server.auth.apple.*;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class AppleIdTokenVerifier {

    private final AppleProperties props;
    private final JWKSource<SecurityContext> appleJwkSource;


    public AppleIdTokenVerifier(AppleProperties props, JWKSource<SecurityContext> appleJwkSource) {
        this.props = props;
        this.appleJwkSource = appleJwkSource;
    }

    public AppleUserIdentity verify(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWSHeader header = signedJWT.getHeader();

            // 1. 캐싱된 소스에서 키 선택 (메모리 효율적 핵심 로직)
            JWKMatcher matcher = new JWKMatcher.Builder()
                    .keyID(header.getKeyID())
                    .build();

            List<JWK> jwks = appleJwkSource.get(new JWKSelector(matcher), null);

            if (jwks.isEmpty()) {
                throw new RuntimeException("No matching JWK found");
            }

            // 2. RSAKey 타입 변환 및 검증기 생성
            RSAKey rsaKey = jwks.getFirst().toRSAKey();
            JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid signature");
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            if (!props.issuer().equals(claims.getIssuer())) {
                throw new RuntimeException("Invalid issuer");
            }

            if (!claims.getAudience().contains(props.clientId())) {
                throw new RuntimeException("Invalid audience");
            }

            if (claims.getExpirationTime().toInstant().isBefore(Instant.now())) {
                throw new RuntimeException("Token expired");
            }

            return new AppleUserIdentity(
                    claims.getSubject(),
                    claims.getStringClaim("email")
            );

        } catch (Exception e) {
            throw new RuntimeException("Apple token invalid", e);
        }
    }
}