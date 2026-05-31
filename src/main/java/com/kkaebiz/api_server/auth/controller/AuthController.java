package com.kkaebiz.api_server.auth.controller;

import com.kkaebiz.api_server.auth.dto.LoginResponse;
import com.kkaebiz.api_server.auth.dto.Provider;
import com.kkaebiz.api_server.auth.dto.SocialLoginRequest;
import com.kkaebiz.api_server.auth.service.AuthFacadeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthFacadeService authFacadeService;

    public AuthController(AuthFacadeService authFacadeService) {
        this.authFacadeService = authFacadeService;
    }

    // ✅ 신규 통합
    @PostMapping("/social/login")
    public LoginResponse socialLogin(@RequestBody @Valid SocialLoginRequest request) {
        return authFacadeService.socialLogin(request);
    }

    // ✅ 기존 카카오 하위호환
    public record KakaoLoginRequest(@NotBlank String kakaoAccessToken) {}

    @PostMapping("/kakao")
    public LoginResponse kakao(@RequestBody @Valid KakaoLoginRequest req) {
        return authFacadeService.socialLogin(new SocialLoginRequest(
                Provider.KAKAO,
                req.kakaoAccessToken(),
                null
        ));
    }
}