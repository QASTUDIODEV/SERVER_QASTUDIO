package qastudio.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qastudio.backend.domain.auth.dto.request.LoginRequest;
import qastudio.backend.domain.auth.dto.response.LoginResponse;
import qastudio.backend.domain.auth.service.AuthService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.jwt.TokenInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class LoginController {

    private final AuthService authService;

    @Operation(
            summary = "User 자체 로그인 API",
            description = "사용자가 자체 로그인을 합니다."
    )
    @PostMapping("/login/local")
    public ApiResponse<TokenInfo> LocalLogin(@RequestBody @Valid LoginRequest loginRequest) {
        TokenInfo loginResponse = authService.localLogin(loginRequest);
        return ApiResponse.onSuccess(loginResponse);
    }
}
