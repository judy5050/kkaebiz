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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 20)
    private String gaebiz;

    @Column(name = "time_seconds", nullable = false)
    private Long timeSeconds;

    @Column(nullable = false, length = 20)
    private String mode;

    @Column(name = "concentrate_type", length = 20)
    private String concentrateType;

    @Column(name = "play_at", nullable = false)
    private LocalDateTime playAt;

    @Column(name = "rest_level", nullable = false)
    private Integer restLevel;

    @Column(name = "created_at", nullable = false)
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
