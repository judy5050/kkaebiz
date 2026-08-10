package com.kkaebiz.api_server.timer.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;


public record TimerRecordItem(

//        @NotNull
//        Long userId,

        @NotNull
        String gaebiz,

        @NotNull
        Long time,

        @NotNull
        String mode,

        ConcentrateType concentrateType,

        @NotNull
        LocalDateTime playAt,

        Integer restLevel
) {
}
