package com.kkaebiz.api_server.statistics;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountResponse;
import com.kkaebiz.api_server.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}