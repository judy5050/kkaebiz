package com.kkaebiz.api_server.statistics.dto;

import java.util.List;

public record ConcentrationPlayRecord(
        String yearMonth,
        List<Boolean> dayRecord
) {
}
