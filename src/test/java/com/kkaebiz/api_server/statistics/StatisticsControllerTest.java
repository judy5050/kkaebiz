package com.kkaebiz.api_server.statistics;

import com.kkaebiz.api_server.common.ApiResult;
import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem;
import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountResponse;
import com.kkaebiz.api_server.statistics.dto.StatisticsCardResult;
import com.kkaebiz.api_server.statistics.service.StatisticsService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatisticsControllerTest {

    @Test
    void returnsCharacterSelectionCountUsingCommonApiResult() {
        StatisticsService service = mock(StatisticsService.class);
        StatisticsController controller = new StatisticsController(service);
        CharacterSelectionCountResponse selectionCount = new CharacterSelectionCountResponse(
                java.util.List.of(new CharacterSelectionCountItem("KIKI", 10L))
        );
        when(service.getSelectionCount(1L)).thenReturn(selectionCount);

        ApiResult<CharacterSelectionCountResponse> response = controller.getSelectionCount(1L);

        verify(service).getSelectionCount(1L);
        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("캐릭터 선택 횟수 조회에 성공했습니다.");
        assertThat(response.data()).isEqualTo(selectionCount);
    }

    @Test
    void returnsCardResultUsingCommonApiResult() {
        StatisticsService service = mock(StatisticsService.class);
        StatisticsController controller = new StatisticsController(service);
        StatisticsCardResult cardResult = new StatisticsCardResult(0, "2026-08-10");
        when(service.getCardArea(1L)).thenReturn(cardResult);

        ApiResult<StatisticsCardResult> response = controller.getCardArea(1L);

        verify(service).getCardArea(1L);
        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("상단 카드 조회에 성공했습니다.");
        assertThat(response.data()).isEqualTo(cardResult);
    }
}
