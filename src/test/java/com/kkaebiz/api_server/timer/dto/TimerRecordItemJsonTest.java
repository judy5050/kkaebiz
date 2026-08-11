package com.kkaebiz.api_server.timer.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimerRecordItemJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void acceptsPlayAtWithSpaceAnd24HourTime() {
        TimerRecordItem item = objectMapper.readValue(requestJson("2026-06-05 23:00:00"), TimerRecordItem.class);

        assertThat(item.playAt()).isEqualTo(LocalDateTime.of(2026, 6, 5, 23, 0));
    }

    @Test
    void rejectsPlayAtWithIsoTSeparator() {
        assertThatThrownBy(() -> objectMapper.readValue(
                requestJson("2026-06-05T23:00:00"), TimerRecordItem.class))
                .isInstanceOf(RuntimeException.class);
    }

    private String requestJson(String playAt) {
        return """
                {
                  "gaebiz": "KIKI",
                  "time": 600,
                  "mode": "REST",
                  "concentrateType": null,
                  "playAt": "%s",
                  "restLevel": 2
                }
                """.formatted(playAt);
    }
}
