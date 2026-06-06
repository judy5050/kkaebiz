package com.kkaebiz.api_server.statistics;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountResponse;
import com.kkaebiz.api_server.statistics.dto.StatisticsCardResponse;
import com.kkaebiz.api_server.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService service;

    @GetMapping("/character-selection-count")
    public CharacterSelectionCountResponse getSelectionCount(@AuthenticationPrincipal Long userId
    ) {
        return service.getSelectionCount(userId);
    }

    @GetMapping("/card")
    public StatisticsCardResponse getCardArea(@AuthenticationPrincipal Long userId) {
        try {
            return service.getCardArea(userId);
        }catch (Exception e) {
            log.error("상단 카드내역 조회 실패 error: ", e);
            return new StatisticsCardResponse(null);
        }

    }
}