package com.kkaebiz.api_server.auth.service;

import com.kkaebiz.api_server.auth.dto.LoginResponse;
import com.kkaebiz.api_server.auth.dto.Provider;
import com.kkaebiz.api_server.auth.dto.SocialLoginRequest;
import com.kkaebiz.api_server.external.kakao.KakaoApiClient;
import com.kkaebiz.api_server.jwt.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthFacadeService {

    private final KakaoApiClient kakaoApiClient;
    private final AppleIdTokenVerifier appleIdTokenVerifier;
    private final SocialLoginService socialLoginService;
    private final JwtService jwtService; // ✅ 너 기존 long 받는 서비스

    public AuthFacadeService(KakaoApiClient kakaoApiClient,
                             AppleIdTokenVerifier appleIdTokenVerifier,
                             SocialLoginService socialLoginService,
                             JwtService jwtService) {
        this.kakaoApiClient = kakaoApiClient;
        this.appleIdTokenVerifier = appleIdTokenVerifier;
        this.socialLoginService = socialLoginService;
        this.jwtService = jwtService;
    }

    public LoginResponse socialLogin(SocialLoginRequest req) {

        long userId = switch (req.provider()) {
            case KAKAO -> {
                long kakaoId = kakaoApiClient.getKakaoId(req.token());
                yield socialLoginService.findOrCreateUserId(
                        Provider.KAKAO, String.valueOf(kakaoId)
                );
            }
            case APPLE -> {
                String appleSub = appleIdTokenVerifier.verify(req.token()).sub();
                yield socialLoginService.findOrCreateUserId(
                        Provider.APPLE, appleSub
                );
            }
        };

        String access = jwtService.createAccessToken(userId);
//        String refresh = jwtService.createRefreshToken(userId);
        return new LoginResponse(access, userId);
    }

    private static void requireAuthCode(String authorizationCode) {
        if (authorizationCode == null || authorizationCode.isBlank()) {
            throw new IllegalArgumentException("APPLE login requires authorizationCode");
        }
    }
}