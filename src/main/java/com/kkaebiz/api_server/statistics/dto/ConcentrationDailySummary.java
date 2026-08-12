package com.kkaebiz.api_server.statistics.dto;

import java.time.LocalDate;

public interface ConcentrationDailySummary {
    LocalDate getPlayDate();

    Long getTotalTime();
}
