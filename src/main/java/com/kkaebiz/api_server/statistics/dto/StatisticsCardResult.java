package com.kkaebiz.api_server.statistics.dto;

public record StatisticsCardResult(
        int streakDays,
        String lastAttendanceDate
) {
}