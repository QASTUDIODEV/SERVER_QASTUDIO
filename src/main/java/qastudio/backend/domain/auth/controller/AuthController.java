package qastudio.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.service.AuthService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.jwt.TokenInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User 자체 회원가입 API | by 지지",
            description = "사용자가 자체 회원가입을 합니다."
    )
    @PostMapping("/sign-up")
    public ApiResponse<Void> UserSignUp(@RequestBody @Valid AuthRequest authRequest) {
        authService.userSignUp(authRequest);
        return ApiResponse.onSuccess(null);
    }

    @Operation(
            summary = "User 자체 로그인 API | by 지지",
            description = "사용자가 자체 로그인을 합니다."
    )
    @PostMapping("/login/local")
    public ApiResponse<TokenInfo> LocalLogin(@RequestBody @Valid AuthRequest authRequest) {
        TokenInfo loginResponse = authService.localLogin(authRequest);
        return ApiResponse.onSuccess(loginResponse);
    }
}
