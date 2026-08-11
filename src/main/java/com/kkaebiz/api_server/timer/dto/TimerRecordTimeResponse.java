package com.kkaebiz.api_server.timer.dto;

public record TimerRecordTimeResponse(
        String mode,
        String concentrateType,
        String timeType,
        long time
) {
}
