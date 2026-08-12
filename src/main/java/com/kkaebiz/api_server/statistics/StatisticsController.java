package com.kkaebiz.api_server.statistics;

import com.kkaebiz.api_server.common.ApiResult;
import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountResponse;
import com.kkaebiz.api_server.statistics.dto.StatisticsCardResult;
import com.kkaebiz.api_server.statistics.dto.ConcentrationCalendarMonthResult;
import com.kkaebiz.api_server.statistics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService service;

    @GetMapping("/character-selection-count")
    public ApiResult<CharacterSelectionCountResponse> getSelectionCount(
            @AuthenticationPrincipal Long userId
    ) {
        CharacterSelectionCountResponse result = service.getSelectionCount(userId);
        return new ApiResult<>(true, "캐릭터 선택 횟수 조회에 성공했습니다.", result);
    }

    @GetMapping("/concentration-calendar")
    public ApiResult<List<ConcentrationCalendarMonthResult>> getConcentrationCalendar(
            @AuthenticationPrincipal Long userId,
            @RequestParam String yearMonth
    ) {
        List<ConcentrationCalendarMonthResult> result =
                service.getConcentrationCalendar(userId, yearMonth);
        return new ApiResult<>(true, "집중 캘린더 조회에 성공했습니다.", result);
    }

    @GetMapping("/card")
    public ApiResult<StatisticsCardResult> getCardArea(@AuthenticationPrincipal Long userId) {
        StatisticsCardResult result = service.getCardArea(userId);
        return new ApiResult<>(true, "상단 카드 조회에 성공했습니다.", result);
    }
}
