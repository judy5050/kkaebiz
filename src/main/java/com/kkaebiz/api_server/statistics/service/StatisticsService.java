package com.kkaebiz.api_server.statistics.service;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem;
import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountResponse;
import com.kkaebiz.api_server.statistics.dto.StatisticsCardResult;
import com.kkaebiz.api_server.statistics.dto.ConcentrationCalendarMonthResult;
import com.kkaebiz.api_server.statistics.dto.ConcentrationDailySummary;
import com.kkaebiz.api_server.statistics.dto.ConcentrationPlayRecord;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final TimerRecordRepository timerRecordRepository;

    public List<ConcentrationCalendarMonthResult> getConcentrationCalendar(
            Long userId,
            String requestedYearMonth
    ) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        YearMonth centerMonth = parseYearMonth(requestedYearMonth);
        YearMonth firstMonth = centerMonth.minusMonths(1);
        YearMonth lastMonth = centerMonth.plusMonths(1);

        List<ConcentrationDailySummary> summaries =
                timerRecordRepository.findConcentrationDailySummaries(
                        userId,
                        firstMonth.atDay(1).atStartOfDay(),
                        lastMonth.plusMonths(1).atDay(1).atStartOfDay()
                );

        Map<LocalDate, Long> timeByDate = summaries.stream()
                .collect(Collectors.toMap(
                        ConcentrationDailySummary::getPlayDate,
                        ConcentrationDailySummary::getTotalTime
                ));

        return List.of(
                calendarMonth(firstMonth, timeByDate),
                calendarMonth(centerMonth, timeByDate),
                calendarMonth(lastMonth, timeByDate)
        );
    }

    public CharacterSelectionCountResponse getSelectionCount(Long userId) {

        List<CharacterSelectionCountItem> result =
                timerRecordRepository.findCharacterCount(userId);

        Map<String, Long> countMap = result.stream()
                .collect(Collectors.toMap(
                        CharacterSelectionCountItem::gaebiz,
                        CharacterSelectionCountItem::count
                ));

        List<CharacterSelectionCountItem> counts = List.of(
                new CharacterSelectionCountItem("KIKI", countMap.getOrDefault("KIKI", 0L)),
                new CharacterSelectionCountItem("BOBO", countMap.getOrDefault("BOBO", 0L)),
                new CharacterSelectionCountItem("NANA", countMap.getOrDefault("NANA", 0L)),
                new CharacterSelectionCountItem("CHACHA", countMap.getOrDefault("CHACHA", 0L)),
                new CharacterSelectionCountItem("BOOBOO", countMap.getOrDefault("BOOBOO", 0L))
        );

        return new CharacterSelectionCountResponse(counts);

    }

    public StatisticsCardResult getCardArea(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate monthStartDate = YearMonth.from(today).atDay(1);

        LocalDateTime startDateTime = monthStartDate.atStartOfDay();
        LocalDateTime endDateTime = today.plusDays(1).atStartOfDay();

        List<LocalDate> attendanceDates =
                timerRecordRepository.findAttendanceDates(
                        userId,
                        startDateTime,
                        endDateTime
                );

        Set<LocalDate> dateSet = new HashSet<>(attendanceDates);

        LocalDate lastAttendanceDate =
                attendanceDates.isEmpty()
                        ? null
                        : attendanceDates.getFirst();

        LocalDate streakBaseDate = getStreakBaseDate(today, dateSet);

        int streakDays = 0;

        if (streakBaseDate != null) {
            streakDays = calculateStreakDays(
                    streakBaseDate,
                    monthStartDate,
                    dateSet
            );
        }

        return new StatisticsCardResult(
                streakDays,
                streakDays >= 1 ? null : (lastAttendanceDate != null ? lastAttendanceDate.toString() : null)
        );
    }

    private LocalDate getStreakBaseDate(LocalDate today, Set<LocalDate> dateSet) {
        if (dateSet.contains(today)) {
            return today;
        }

        LocalDate yesterday = today.minusDays(1);

        if (dateSet.contains(yesterday)) {
            return yesterday;
        }

        return null;
    }

    private int calculateStreakDays(
            LocalDate lastAttendanceDate,
            LocalDate monthStartDate,
            Set<LocalDate> dateSet
    ) {
        int streakDays = 0;
        LocalDate cursor = lastAttendanceDate;

        while (!cursor.isBefore(monthStartDate) && dateSet.contains(cursor)) {
            streakDays++;
            cursor = cursor.minusDays(1);
        }

        return streakDays;
    }

    private YearMonth parseYearMonth(String value) {
        if (value == null || !value.matches("\\d{4}-(0[1-9]|1[0-2])")) {
            throw new IllegalArgumentException("yearMonth는 yyyy-MM 형식이어야 합니다.");
        }

        try {
            return YearMonth.parse(value);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("유효하지 않은 yearMonth입니다.");
        }
    }

    private ConcentrationCalendarMonthResult calendarMonth(
            YearMonth yearMonth,
            Map<LocalDate, Long> timeByDate
    ) {
        List<Boolean> dayRecord = new ArrayList<>(yearMonth.lengthOfMonth());
        LocalDate feverDay = null;
        long maximumTime = 0L;

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            long totalTime = timeByDate.getOrDefault(date, 0L);
            dayRecord.add(totalTime > 0L);

            if (totalTime > maximumTime) {
                maximumTime = totalTime;
                feverDay = date;
            }
        }

        return new ConcentrationCalendarMonthResult(
                new ConcentrationPlayRecord(yearMonth.toString(), dayRecord),
                feverDay == null ? null : feverDay.toString()
        );
    }
}
