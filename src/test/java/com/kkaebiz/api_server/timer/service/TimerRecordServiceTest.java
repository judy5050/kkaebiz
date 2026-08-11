package com.kkaebiz.api_server.timer.service;

import com.kkaebiz.api_server.timer.domain.TimerRecord;
import com.kkaebiz.api_server.timer.dto.ConcentrateType;
import com.kkaebiz.api_server.timer.dto.TimerRecordItem;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.dto.TimerRecordPeriodSummary;
import com.kkaebiz.api_server.timer.dto.TimerRecordTimeListResponse;
import com.kkaebiz.api_server.timer.dto.TimerRecordTimeResponse;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class TimerRecordServiceTest {

    private static final LocalDateTime PLAY_AT = LocalDateTime.of(2026, 8, 10, 12, 0);

    @Mock
    private TimerRecordRepository timerRecordRepository;

    @InjectMocks
    private TimerRecordService timerRecordService;

    @Test
    void returnsAllTimerAreasAndFillsMissingTimesWithZero() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        LocalDate weekEnd = weekStart.plusWeeks(1);
        LocalDate queryStart = monthStart.isBefore(weekStart) ? monthStart : weekStart;
        LocalDate queryEnd = monthStart.plusMonths(1).isAfter(weekEnd) ? monthStart.plusMonths(1) : weekEnd;
        when(timerRecordRepository.findPeriodTimeSummaries(
                1L,
                queryStart.atStartOfDay(),
                queryEnd.atStartOfDay(),
                monthStart.atStartOfDay(),
                monthStart.plusMonths(1).atStartOfDay(),
                weekStart.atStartOfDay(),
                weekEnd.atStartOfDay(),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        )).thenReturn(List.of(
                new TimerRecordPeriodSummary("REST", null, 3000L, 1200L, 300L),
                new TimerRecordPeriodSummary("CONCENTRATE", "STUDY", 6000L, 2400L, 600L)
        ));

        TimerRecordTimeListResponse response = timerRecordService.getTimeRecords(1L);

        assertThat(response.result()).containsExactly(
                new TimerRecordTimeResponse("REST", null, "month", 3000L),
                new TimerRecordTimeResponse("REST", null, "week", 1200L),
                new TimerRecordTimeResponse("REST", null, "day", 300L),
                new TimerRecordTimeResponse("CONCENTRATE", "NORMAL", "month", 0L),
                new TimerRecordTimeResponse("CONCENTRATE", "NORMAL", "week", 0L),
                new TimerRecordTimeResponse("CONCENTRATE", "NORMAL", "day", 0L),
                new TimerRecordTimeResponse("CONCENTRATE", "STUDY", "month", 6000L),
                new TimerRecordTimeResponse("CONCENTRATE", "STUDY", "week", 2400L),
                new TimerRecordTimeResponse("CONCENTRATE", "STUDY", "day", 600L),
                new TimerRecordTimeResponse("CONCENTRATE", "EXERCISE", "month", 0L),
                new TimerRecordTimeResponse("CONCENTRATE", "EXERCISE", "week", 0L),
                new TimerRecordTimeResponse("CONCENTRATE", "EXERCISE", "day", 0L)
        );
    }

    @Test
    void queriesMonthOnceWithDayAndMondayBoundaries() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1L);
        LocalDate weekEnd = monday.plusWeeks(1);
        LocalDate queryStart = monthStart.isBefore(monday) ? monthStart : monday;
        LocalDate queryEnd = monthStart.plusMonths(1).isAfter(weekEnd) ? monthStart.plusMonths(1) : weekEnd;
        when(timerRecordRepository.findPeriodTimeSummaries(
                1L,
                queryStart.atStartOfDay(),
                queryEnd.atStartOfDay(),
                monthStart.atStartOfDay(),
                monthStart.plusMonths(1).atStartOfDay(),
                monday.atStartOfDay(),
                weekEnd.atStartOfDay(),
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay()
        )).thenReturn(List.of());

        timerRecordService.getTimeRecords(1L);

        verify(timerRecordRepository).findPeriodTimeSummaries(
                eq(1L),
                eq(queryStart.atStartOfDay()),
                eq(queryEnd.atStartOfDay()),
                eq(monthStart.atStartOfDay()),
                eq(monthStart.plusMonths(1).atStartOfDay()),
                eq(monday.atStartOfDay()),
                eq(weekEnd.atStartOfDay()),
                eq(today.atStartOfDay()),
                eq(today.plusDays(1).atStartOfDay())
        );
    }

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
