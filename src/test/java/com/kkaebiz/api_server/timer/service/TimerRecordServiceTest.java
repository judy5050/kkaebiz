package com.kkaebiz.api_server.timer.service;

import com.kkaebiz.api_server.timer.domain.TimerRecord;
import com.kkaebiz.api_server.timer.dto.ConcentrateType;
import com.kkaebiz.api_server.timer.dto.TimerRecordItem;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TimerRecordServiceTest {

    private static final LocalDateTime PLAY_AT = LocalDateTime.of(2026, 8, 10, 12, 0);

    @Mock
    private TimerRecordRepository timerRecordRepository;

    @InjectMocks
    private TimerRecordService timerRecordService;

    @Test
    void savesRequestedRestLevelForRestModeWithoutConcentrateType() {
        TimerRecordItem item = item("REST", null, 7);

        timerRecordService.save(1L, new TimerRecordSaveRequest(List.of(item)));

        ArgumentCaptor<TimerRecord> captor = ArgumentCaptor.forClass(TimerRecord.class);
        verify(timerRecordRepository).save(captor.capture());
        assertThat(captor.getValue().getRestLevel()).isEqualTo(7);
        assertThat(captor.getValue().getConcentrateType()).isNull();
    }

    @Test
    void rejectsRestModeWithoutRestLevel() {
        TimerRecordItem item = item("REST", null, null);

        assertThatThrownBy(() -> timerRecordService.save(
                1L, new TimerRecordSaveRequest(List.of(item))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("restLevel");
    }

    @Test
    void savesZeroForConcentrateModeRegardlessOfRequestedRestLevel() {
        TimerRecordItem omitted = item("CONCENTRATE", ConcentrateType.STUDY, null);
        TimerRecordItem supplied = item("CONCENTRATE", ConcentrateType.NORMAL, 9);

        timerRecordService.save(1L, new TimerRecordSaveRequest(List.of(omitted, supplied)));

        ArgumentCaptor<TimerRecord> captor = ArgumentCaptor.forClass(TimerRecord.class);
        verify(timerRecordRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(TimerRecord::getRestLevel)
                .containsExactly(0, 0);
    }

    @Test
    void rejectsConcentrateModeWithoutConcentrateType() {
        TimerRecordItem item = item("CONCENTRATE", null, 3);

        assertThatThrownBy(() -> timerRecordService.save(
                1L, new TimerRecordSaveRequest(List.of(item))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("concentrateType");
    }

    private TimerRecordItem item(
            String mode,
            ConcentrateType concentrateType,
            Integer restLevel
    ) {
        return new TimerRecordItem(
                "KIKI",
                600L,
                mode,
                concentrateType,
                PLAY_AT,
                restLevel
        );
    }
}
