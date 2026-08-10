package com.kkaebiz.api_server.timer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TimerRecordSaveRequest(
        @NotEmpty
        List<@Valid TimerRecordItem> records
) {
}
