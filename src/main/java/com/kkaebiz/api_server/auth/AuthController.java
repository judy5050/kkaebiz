package com.kkaebiz.api_server.auth;

import com.kkaebiz.api_server.auth.dto.KakaoLoginRequest;
import com.kkaebiz.api_server.auth.dto.LoginResponse;
import com.kkaebiz.api_server.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/kakao")
    public LoginResponse kakaoLogin(@RequestBody @Valid KakaoLoginRequest req) {
        return authService.loginWithKakao(req.kakaoAccessToken());
    }
}