package com.kkaebiz.api_server.auth.service;

import com.kkaebiz.api_server.auth.dto.LoginResponse;
import com.kkaebiz.api_server.external.kakao.KakaoApiClient;
import com.kkaebiz.api_server.jwt.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final KakaoApiClient kakaoApiClient;
    private final JwtService jwtService;

    public AuthService(KakaoApiClient kakaoApiClient, JwtService jwtService) {
        this.kakaoApiClient = kakaoApiClient;
        this.jwtService = jwtService;
    }

    public LoginResponse loginWithKakao(String kakaoAccessToken) {
        long kakaoId = kakaoApiClient.getKakaoId(kakaoAccessToken);

        // DB 없으니 일단 kakaoId를 subject로 사용 (나중에 DB PK로 바꾸면 됨)
        String access = jwtService.createAccessToken(kakaoId);
//        String refresh = jwtService.createRefreshToken(kakaoId);

        return new LoginResponse(access, kakaoId);
    }
}