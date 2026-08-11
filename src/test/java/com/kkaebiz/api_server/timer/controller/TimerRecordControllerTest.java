package com.kkaebiz.api_server.timer.controller;

import com.kkaebiz.api_server.common.ApiResult;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.dto.TimerRecordTimeListResponse;
import com.kkaebiz.api_server.timer.dto.TimerRecordTimeResponse;
import com.kkaebiz.api_server.timer.service.TimerRecordService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TimerRecordControllerTest {

    @Test
    void returnsTimerTimeRecordsForAuthenticatedUser() {
        TimerRecordService service = mock(TimerRecordService.class);
        TimerRecordController controller = new TimerRecordController(service);
        TimerRecordTimeListResponse records = new TimerRecordTimeListResponse(List.of(
                new TimerRecordTimeResponse("REST", null, "day", 300L)
        ));
        when(service.getTimeRecords(1L)).thenReturn(records);

        TimerRecordTimeListResponse response = controller.getTimerRecords(1L);

        verify(service).getTimeRecords(1L);
        assertThat(response).isEqualTo(records);
    }

    @Test
    void returnsCommonApiResultWithoutRestLevel() {
        TimerRecordService service = mock(TimerRecordService.class);
        TimerRecordController controller = new TimerRecordController(service);
        TimerRecordSaveRequest request = new TimerRecordSaveRequest(List.of());

        ApiResult<Void> response = controller.saveTimerRecords(1L, request);

        verify(service).save(1L, request);
        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("타이머 저장에 성공했습니다.");
        assertThat(response.data()).isNull();
    }
}
