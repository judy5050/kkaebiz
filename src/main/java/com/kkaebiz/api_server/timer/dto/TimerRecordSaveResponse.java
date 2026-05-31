package com.kkaebiz.api_server.timer.dto;

import lombok.Builder;

@Builder
public record TimerRecordSaveResponse(
        Integer restLevel,
        String resultMsg
) {
}