package com.kkaebiz.api_server.statistics.service;

import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountItem;
import com.kkaebiz.api_server.statistics.dto.CharacterSelectionCountResponse;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
}
