package com.kkaebiz.api_server.auth.service;

import com.kkaebiz.api_server.auth.apple.*;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;

@Component
public class AppleIdTokenVerifier {

    private final AppleProperties props;

    public AppleIdTokenVerifier(AppleProperties props) {
        this.props = props;
    }

    public AppleUserIdentity verify(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            JWKSet jwkSet = JWKSet.load(new URL(props.jwksUrl()));
            JWK jwk = jwkSet.getKeyByKeyId(signedJWT.getHeader().getKeyID());

            if (jwk == null) {
                throw new RuntimeException("No matching JWK");
            }

        // 2. RSAKey로 타입 캐스팅 (애플은 RSA를 사용하므로 안전합니다)
            RSAKey rsaKey = jwk.toRSAKey();

        // 3. 이제 toRSAPublicKey()를 호출할 수 있습니다.
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