package com.kkaebiz.api_server.timer.service;

import com.kkaebiz.api_server.timer.domain.TimerRecord;
import com.kkaebiz.api_server.timer.dto.TimerRecordItem;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimerRecordService {

    private final TimerRecordRepository timerRecordRepository;

    public void save(Long userId, TimerRecordSaveRequest requests) {

        for (TimerRecordItem request : requests.records()) {
            validate(userId,request);


            TimerRecord timerRecord = TimerRecord.builder()
                    .userId(userId)
                    .gaebiz(request.gaebiz())
                    .timeSeconds(request.time())
                    .mode(request.mode())
                    .concentrateType(request.concentrateType() == null
                            ? null
                            : request.concentrateType().name())
                    .playAt(request.playAt())
                    .restLevel(resolveRestLevel(request))
                    .build();

            timerRecordRepository.save(timerRecord);
        }


    }

    private void validate(Long userId, TimerRecordItem request) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        if (request.time() == null || request.time() <= 0) {
            throw new IllegalArgumentException("time은 0보다 커야 합니다.");
        }

        if (request.mode() == null) {
            throw new IllegalArgumentException("mode는 필수입니다.");
        }

        if ("CONCENTRATE".equals(request.mode())
                && request.concentrateType() == null) {
            throw new IllegalArgumentException("집중모드일 경우 concentrateType은 필수입니다.");
        }

        if ("REST".equals(request.mode()) && request.restLevel() == null) {
            throw new IllegalArgumentException("휴식모드일 경우 restLevel은 필수입니다.");
        }
    }

    private Integer resolveRestLevel(TimerRecordItem request) {
        return "REST".equals(request.mode()) ? request.restLevel() : 0;
    }
}
