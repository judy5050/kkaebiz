package com.kkaebiz.api_server.timer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "timer_record")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimerRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String gaebiz;

    @Column(name = "time_seconds")
    private Long timeSeconds;

    private String mode;

    private String concentrateType;

    private LocalDateTime playAt;

    private Integer restLevel;

    private LocalDateTime createdAt;

    @Builder
    public TimerRecord(
            Long userId,
            String gaebiz,
            Long timeSeconds,
            String mode,
            String concentrateType,
            LocalDateTime playAt,
            Integer restLevel
    ) {
        this.userId = userId;
        this.gaebiz = gaebiz;
        this.timeSeconds = timeSeconds;
        this.mode = mode;
        this.concentrateType = concentrateType;
        this.playAt = playAt;
        this.restLevel = restLevel;
        this.createdAt = LocalDateTime.now();
    }
}