package com.kkaebiz.api_server.timer.repository;

import com.kkaebiz.api_server.timer.domain.TimerRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerRecordRepository extends JpaRepository<TimerRecord, Long> {
}