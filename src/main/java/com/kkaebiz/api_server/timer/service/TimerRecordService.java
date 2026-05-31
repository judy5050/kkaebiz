package com.kkaebiz.api_server.timer.service;

import com.kkaebiz.api_server.timer.domain.TimerRecord;
import com.kkaebiz.api_server.timer.dto.TimerRecordItem;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveResponse;
import com.kkaebiz.api_server.timer.repository.TimerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TimerRecordService {

    private final TimerRecordRepository timerRecordRepository;

    public TimerRecordSaveResponse save(Long userId, TimerRecordSaveRequest requests) {

        for (TimerRecordItem request : requests.records()) {
            validate(userId,request);


            TimerRecord timerRecord = TimerRecord.builder()
                    .userId(userId)
                    .gaebiz(request.gaebiz())
                    .timeSeconds(request.time())
                    .mode(request.mode())
                    .concentrateType(request.concentrateType().name())
                    .playAt(request.playAt())
                    .restLevel(0)
                    .build();

            timerRecordRepository.save(timerRecord);
        }


        return TimerRecordSaveResponse.builder()
                .restLevel(0)
                .resultMsg("타이머 저장에 성공했습니다.")
                .build();
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
    }

    private Integer calculateRestLevel(TimerRecordItem request) {
        if (!"REST".equals(request.mode())) {
            return null;
        }

        long minutes = request.time() / 60;

        if (minutes < 5) return 1;
        if (minutes < 15) return 2;
        if (minutes < 30) return 3;
        return 4;
    }
}