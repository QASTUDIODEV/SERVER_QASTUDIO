package qastudio.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qastudio.backend.domain.auth.dto.request.SignUpRequest;
import qastudio.backend.domain.auth.service.AuthService;
import qastudio.backend.global.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class SignUpController {

    private final AuthService authService;

    @Operation(
            summary = "User 자체 회원가입",
            description = "이동봉사자 자체 회원가입을 합니다."
    )
    @PostMapping("/sign-up")
    public ApiResponse<Void> UserSignUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        authService.userSignUp(signUpRequest);
        return ApiResponse.onSuccess(null);
    }
}