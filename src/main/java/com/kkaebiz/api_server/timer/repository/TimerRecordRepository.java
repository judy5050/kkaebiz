package com.kkaebiz.api_server.timer.repository;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem;
import com.kkaebiz.api_server.timer.domain.TimerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}