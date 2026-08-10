package com.kkaebiz.api_server.timer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull
        LocalDateTime playAt,

        Integer restLevel
) {
}
