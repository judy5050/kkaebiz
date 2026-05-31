package com.kkaebiz.api_server.user;

import com.kkaebiz.api_server.common.ApiResult;
import com.kkaebiz.api_server.user.dto.UpdateNicknameRequest;
import com.kkaebiz.api_server.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/nickname")
    public ResponseEntity updateNickname(@AuthenticationPrincipal Long userId,@RequestBody UpdateNicknameRequest request) {
        userService.updateNickname(userId, request.getNickname());
        return ResponseEntity.ok(new ApiResult<>(true,"닉네임 변경 성공!", null));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResult<Void>> withdraw(@AuthenticationPrincipal Long userId) {
        // 1. 토큰에서 추출된 userId를 서비스로 전달
        userService.withdraw(userId);

        // 2. 탈퇴 성공 응답 (메시지 포함)
        return ResponseEntity.ok(new ApiResult<>(true, "회원 탈퇴가 완료되었습니다.", null));
    }
}
