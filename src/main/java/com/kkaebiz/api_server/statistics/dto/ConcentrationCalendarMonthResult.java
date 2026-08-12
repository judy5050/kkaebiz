package com.kkaebiz.api_server.statistics.dto;

public record ConcentrationCalendarMonthResult(
        ConcentrationPlayRecord playRecord,
        String feverDay
) {
}
