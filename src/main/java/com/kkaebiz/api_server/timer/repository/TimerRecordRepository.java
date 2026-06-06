package com.kkaebiz.api_server.timer.repository;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem;
import com.kkaebiz.api_server.timer.domain.TimerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TimerRecordRepository extends JpaRepository<TimerRecord, Long> {

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