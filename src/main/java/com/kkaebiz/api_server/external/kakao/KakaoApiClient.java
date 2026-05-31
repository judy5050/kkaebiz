package com.kkaebiz.api_server.external.kakao;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class KakaoApiClient {
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .build();

    public long getKakaoId(String kakaoAccessToken) {
        Map<String, Object> body = restClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        Object id = body.get("id");
        if (id == null) throw new IllegalStateException("Kakao /v2/user/me response missing id");
        return ((Number) id).longValue();
    }
}
