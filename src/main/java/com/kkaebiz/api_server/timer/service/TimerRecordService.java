package com.kkaebiz.api_server.timer.service;

import com.kkaebiz.api_server.timer.domain.TimerRecord;
import com.kkaebiz.api_server.timer.dto.TimerRecordItem;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.dto.TimerRecordPeriodSummary;
import com.kkaebiz.api_server.timer.dto.TimerRecordTimeListResponse;
import com.kkaebiz.api_server.timer.dto.TimerRecordTimeResponse;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TimerRecordService {

    private static final ZoneId SERVICE_ZONE = ZoneId.of("Asia/Seoul");

    private final TimerRecordRepository timerRecordRepository;

    @Transactional(readOnly = true)
    public TimerRecordTimeListResponse getTimeRecords(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        LocalDate today = LocalDate.now(SERVICE_ZONE);
        LocalDate monthStart = YearMonth.from(today).atDay(1);
        LocalDate monthEnd = monthStart.plusMonths(1);
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusWeeks(1);
        LocalDate dayEnd = today.plusDays(1);
        LocalDate queryStart = monthStart.isBefore(weekStart) ? monthStart : weekStart;
        LocalDate queryEnd = monthEnd.isAfter(weekEnd) ? monthEnd : weekEnd;

        Map<String, TimerRecordPeriodSummary> summaries = timerRecordRepository.findPeriodTimeSummaries(
                        userId,
                        queryStart.atStartOfDay(),
                        queryEnd.atStartOfDay(),
                        monthStart.atStartOfDay(),
                        monthEnd.atStartOfDay(),
                        weekStart.atStartOfDay(),
                        weekEnd.atStartOfDay(),
                        today.atStartOfDay(),
                        dayEnd.atStartOfDay()
                ).stream()
                .collect(Collectors.toMap(this::summaryKey, Function.identity()));

        return new TimerRecordTimeListResponse(List.of(
                response("REST", null, "month", summaries),
                response("REST", null, "week", summaries),
                response("REST", null, "day", summaries),
                response("CONCENTRATE", "NORMAL", "month", summaries),
                response("CONCENTRATE", "NORMAL", "week", summaries),
                response("CONCENTRATE", "NORMAL", "day", summaries),
                response("CONCENTRATE", "STUDY", "month", summaries),
                response("CONCENTRATE", "STUDY", "week", summaries),
                response("CONCENTRATE", "STUDY", "day", summaries),
                response("CONCENTRATE", "EXERCISE", "month", summaries),
                response("CONCENTRATE", "EXERCISE", "week", summaries),
                response("CONCENTRATE", "EXERCISE", "day", summaries)
        ));
    }

    public void save(Long userId, TimerRecordSaveRequest requests) {

        for (TimerRecordItem request : requests.records()) {
            validate(userId,request);


            TimerRecord timerRecord = TimerRecord.builder()
                    .userId(userId)
                    .gaebiz(request.gaebiz())
                    .timeSeconds(request.time())
                    .mode(request.mode())
                    .concentrateType(request.concentrateType() == null
                            ? null
                            : request.concentrateType().name())
                    .playAt(request.playAt())
                    .restLevel(resolveRestLevel(request))
                    .build();

            timerRecordRepository.save(timerRecord);
        }


    }

    private void validate(Long userId, TimerRecordItem request) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        if (request.time() == null || request.time() <= 0) {
            throw new IllegalArgumentException("time은 0보다 커야 합니다.");
        }

        if (request.mode() == null) {
            throw new IllegalArgumentException("mode는 필수입니다.");
        }

        if ("CONCENTRATE".equals(request.mode())
                && request.concentrateType() == null) {
            throw new IllegalArgumentException("집중모드일 경우 concentrateType은 필수입니다.");
        }

        if ("REST".equals(request.mode()) && request.restLevel() == null) {
            throw new IllegalArgumentException("휴식모드일 경우 restLevel은 필수입니다.");
        }
    }

    private Integer resolveRestLevel(TimerRecordItem request) {
        return "REST".equals(request.mode()) ? request.restLevel() : 0;
    }

    private TimerRecordTimeResponse response(
            String mode,
            String concentrateType,
            String timeType,
            Map<String, TimerRecordPeriodSummary> summaries
    ) {
        TimerRecordPeriodSummary summary = summaries.get(summaryKey(mode, concentrateType));
        long time = summary == null ? 0L : switch (timeType) {
            case "month" -> summary.monthTime();
            case "week" -> summary.weekTime();
            case "day" -> summary.dayTime();
            default -> throw new IllegalArgumentException("지원하지 않는 timeType입니다.");
        };
        return new TimerRecordTimeResponse(mode, concentrateType, timeType, time);
    }

    private String summaryKey(TimerRecordPeriodSummary summary) {
        return summaryKey(summary.mode(), summary.concentrateType());
    }

    private String summaryKey(String mode, String concentrateType) {
        return mode + ":" + (concentrateType == null ? "" : concentrateType);
    }
}
