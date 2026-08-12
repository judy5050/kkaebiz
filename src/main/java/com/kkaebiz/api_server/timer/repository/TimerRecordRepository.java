package com.kkaebiz.api_server.timer.repository;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem;
import com.kkaebiz.api_server.statistics.dto.ConcentrationDailySummary;
import com.kkaebiz.api_server.timer.domain.TimerRecord;
import com.kkaebiz.api_server.timer.dto.TimerRecordPeriodSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TimerRecordRepository extends JpaRepository<TimerRecord, Long> {

    @Query(value = """
    SELECT DATE(PLAY_AT) AS playDate,
           SUM(TIME_SECONDS) AS totalTime
    FROM TIMER_RECORD
    WHERE USER_ID = :userId
      AND MODE = 'CONCENTRATE'
      AND PLAY_AT >= :startDateTime
      AND PLAY_AT < :endDateTime
    GROUP BY DATE(PLAY_AT)
    ORDER BY playDate
""", nativeQuery = true)
    List<ConcentrationDailySummary> findConcentrationDailySummaries(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
    SELECT new com.kkaebiz.api_server.timer.dto.TimerRecordPeriodSummary(
        t.mode,
        t.concentrateType,
        SUM(CASE WHEN t.playAt >= :monthStartDateTime AND t.playAt < :monthEndDateTime THEN t.timeSeconds ELSE 0L END),
        SUM(CASE WHEN t.playAt >= :weekStartDateTime AND t.playAt < :weekEndDateTime THEN t.timeSeconds ELSE 0L END),
        SUM(CASE WHEN t.playAt >= :dayStartDateTime AND t.playAt < :dayEndDateTime THEN t.timeSeconds ELSE 0L END)
    )
    FROM TimerRecord t
    WHERE t.userId = :userId
      AND t.playAt >= :queryStartDateTime
      AND t.playAt < :queryEndDateTime
    GROUP BY t.mode, t.concentrateType
""")
    List<TimerRecordPeriodSummary> findPeriodTimeSummaries(
            @Param("userId") Long userId,
            @Param("queryStartDateTime") LocalDateTime queryStartDateTime,
            @Param("queryEndDateTime") LocalDateTime queryEndDateTime,
            @Param("monthStartDateTime") LocalDateTime monthStartDateTime,
            @Param("monthEndDateTime") LocalDateTime monthEndDateTime,
            @Param("weekStartDateTime") LocalDateTime weekStartDateTime,
            @Param("weekEndDateTime") LocalDateTime weekEndDateTime,
            @Param("dayStartDateTime") LocalDateTime dayStartDateTime,
            @Param("dayEndDateTime") LocalDateTime dayEndDateTime
    );

    @Query("""
    SELECT new com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem(
        t.gaebiz,
        COUNT(t.id)
    )
    FROM TimerRecord t
    WHERE t.userId = :userId
    GROUP BY t.gaebiz
""")
    List<CharacterSelectionCountItem> findCharacterCount(Long userId);

    @Query(value = """
    SELECT DISTINCT DATE(PLAY_AT) AS attendanceDate
    FROM TIMER_RECORD
    WHERE USER_ID = :userId
      AND PLAY_AT >= :startDateTime
      AND PLAY_AT < :endDateTime
    ORDER BY attendanceDate DESC
""", nativeQuery = true)
    List<LocalDate> findAttendanceDates(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}
