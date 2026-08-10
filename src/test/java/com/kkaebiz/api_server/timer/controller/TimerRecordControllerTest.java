package com.kkaebiz.api_server.timer.controller;

import com.kkaebiz.api_server.common.ApiResult;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.service.TimerRecordService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TimerRecordControllerTest {

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
