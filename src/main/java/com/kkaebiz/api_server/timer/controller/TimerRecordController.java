package com.kkaebiz.api_server.timer.controller;

import com.kkaebiz.api_server.common.ApiResult;
import com.kkaebiz.api_server.timer.dto.TimerRecordSaveRequest;
import com.kkaebiz.api_server.timer.service.TimerRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timers")
public class TimerRecordController {

    private final TimerRecordService timerRecordService;

    @PostMapping("/records")
    public ApiResult<Void> saveTimerRecords(@AuthenticationPrincipal Long userId, @RequestBody @Valid TimerRecordSaveRequest request
    ) {
        timerRecordService.save(userId, request);
        return new ApiResult<>(true, "타이머 저장에 성공했습니다.", null);
    }
}
