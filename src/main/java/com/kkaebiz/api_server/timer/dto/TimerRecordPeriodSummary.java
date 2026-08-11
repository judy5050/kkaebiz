package com.kkaebiz.api_server.timer.dto;

public record TimerRecordPeriodSummary(
        String mode,
        String concentrateType,
        Long monthTime,
        Long weekTime,
        Long dayTime
) {
}
