package com.kkaebiz.api_server.statistics.service;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem;
import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountResponse;
import com.kkaebiz.api_server.statistics.dto.StatisticsCardResponse;
import com.kkaebiz.api_server.statistics.dto.StatisticsCardResult;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final TimerRecordRepository timerRecordRepository;

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

    public StatisticsCardResponse getCardArea(Long userId) {
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

        return new StatisticsCardResponse(
                new StatisticsCardResult(
                        streakDays,
                        streakDays >= 1 ? null : (lastAttendanceDate != null ? lastAttendanceDate.toString() : null)
                )
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
}
