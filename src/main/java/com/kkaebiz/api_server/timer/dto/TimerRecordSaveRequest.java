package com.kkaebiz.api_server.timer.dto;

import java.util.List;

public record TimerRecordSaveRequest(
        List<TimerRecordItem> records
) {
}
