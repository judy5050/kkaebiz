package com.kkaebiz.api_server.statistics.service;

import com.kkaebiz.api_server.statistics.dto.ConcentrationCalendarMonthResult;
import com.kkaebiz.api_server.statistics.dto.ConcentrationDailySummary;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private TimerRecordRepository timerRecordRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void returnsPreviousRequestedAndNextMonthWithDailyRecordsAndFeverDays() {
        ConcentrationDailySummary aprilFirst = summary(LocalDate.of(2026, 4, 1), 600L);
        ConcentrationDailySummary maySecond = summary(LocalDate.of(2026, 5, 2), 300L);
        ConcentrationDailySummary mayThird = summary(LocalDate.of(2026, 5, 3), 900L);
        ConcentrationDailySummary juneLast = summary(LocalDate.of(2026, 6, 30), 1200L);
        when(timerRecordRepository.findConcentrationDailySummaries(
                1L,
                LocalDateTime.of(2026, 4, 1, 0, 0),
                LocalDateTime.of(2026, 7, 1, 0, 0)
        )).thenReturn(List.of(aprilFirst, maySecond, mayThird, juneLast));

        List<ConcentrationCalendarMonthResult> result =
                statisticsService.getConcentrationCalendar(1L, "2026-05");

        assertThat(result).hasSize(3);
        assertThat(result).extracting(item -> item.playRecord().yearMonth())
                .containsExactly("2026-04", "2026-05", "2026-06");
        assertThat(result).extracting(item -> item.playRecord().dayRecord().size())
                .containsExactly(30, 31, 30);
        assertThat(result.get(0).playRecord().dayRecord().get(0)).isTrue();
        assertThat(result.get(1).playRecord().dayRecord().get(1)).isTrue();
        assertThat(result.get(1).playRecord().dayRecord().get(2)).isTrue();
        assertThat(result.get(2).playRecord().dayRecord().get(29)).isTrue();
        assertThat(result).extracting(ConcentrationCalendarMonthResult::feverDay)
                .containsExactly("2026-04-01", "2026-05-03", "2026-06-30");

        verify(timerRecordRepository).findConcentrationDailySummaries(
                1L,
                LocalDateTime.of(2026, 4, 1, 0, 0),
                LocalDateTime.of(2026, 7, 1, 0, 0)
        );
    }

    @Test
    void returnsNullFeverDayAndLeapYearDayCountWithoutRecords() {
        when(timerRecordRepository.findConcentrationDailySummaries(
                1L,
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 4, 1, 0, 0)
        )).thenReturn(List.of());

        List<ConcentrationCalendarMonthResult> result =
                statisticsService.getConcentrationCalendar(1L, "2024-02");

        assertThat(result.get(1).playRecord().dayRecord()).hasSize(29).containsOnly(false);
        assertThat(result).extracting(ConcentrationCalendarMonthResult::feverDay)
                .containsOnlyNulls();
    }

    @Test
    void rejectsInvalidYearMonth() {
        assertThatThrownBy(() -> statisticsService.getConcentrationCalendar(1L, "2026-13"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("yyyy-MM");

        verifyNoInteractions(timerRecordRepository);
    }

    private ConcentrationDailySummary summary(LocalDate date, long totalTime) {
        ConcentrationDailySummary summary = mock(ConcentrationDailySummary.class);
        when(summary.getPlayDate()).thenReturn(date);
        when(summary.getTotalTime()).thenReturn(totalTime);
        return summary;
    }
}
