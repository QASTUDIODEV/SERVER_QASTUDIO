package qastudio.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.EmailResponse;
import qastudio.backend.domain.auth.service.AuthService;
import qastudio.backend.domain.auth.service.EmailService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.jwt.TokenInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

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

    @Operation(summary = "이메일 인증번호 전송", description = "자체 회원가입 시, 입력한 이메일로 인증번호를 전송합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH409", description = "이미 등록된 이메일입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EMAIL400", description = "이메일 인증 코드 전송을 실패했습니다.")
    })
    @PostMapping("/email")
    public ApiResponse<EmailResponse> mailConfirm(@RequestBody @Valid EmailRequest emailRequest){
        EmailResponse emailResponse = emailService.sendEmail(emailRequest);
        return ApiResponse.onSuccess(emailResponse);
    }
}
