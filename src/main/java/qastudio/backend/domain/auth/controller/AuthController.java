package qastudio.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.auth.converter.AuthConverter;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.domain.auth.dto.response.EmailResponse;
import qastudio.backend.domain.auth.service.AuthCommandService;
import qastudio.backend.domain.auth.service.EmailQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.global.security.jwt.TokenInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/auth")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final EmailQueryService emailQueryService;
    private final AuthConverter authConverter;

    @Operation(
            summary = "자체 회원가입 API | by 지지",
            description = "사용자가 자체 회원가입을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH409",
                    description = "Email already registered."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "Invalid request."
            )
    })
    @PostMapping("/sign-up")
    public ApiResponse<String> singUpLocal(@RequestBody @Valid AuthRequest.LocalRequest authRequest, HttpServletResponse response) {
        authCommandService.userSignUp(authRequest, response);
        return ApiResponse.onSuccess("회원가입에 성공하였습니다.");
    }

    @Operation(summary = "이메일 인증번호 전송 API | by 지지", description = "자체 회원가입 시, 입력한 이메일로 인증번호를 전송합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH409", description = "Email already registered."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EMAIL400", description = "Failed to send email verification code.")
    })
    @PostMapping("/sign-up/email")
    public ApiResponse<EmailResponse> sendSignEmail(@RequestBody @Valid EmailRequest emailRequest){
        EmailResponse emailResponse = emailQueryService.sendSignEmail(emailRequest);
        return ApiResponse.onSuccess(emailResponse);
    }

    @Operation(
            summary = "비밀번호 변경 API | by 지지",
            description = "사용자가 자체 로그인 계정의 비밀번호를 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH404",
                    description = "User not found."
            )
    })
    @PostMapping("/update/password")
    public ApiResponse<Void> updatePassword(@RequestBody @Valid AuthRequest.ChangePasswordRequest changePasswordRequest) {
        authCommandService.changePassword(changePasswordRequest);
        return ApiResponse.onSuccess(null);
    }

    @Operation(
            summary = "비밀번호 변경 이메일 인증번호 전송 API | by 지지",
            description = "사용자의 계정(이메일)이 존재하는 지 확인 후, 해당 이메일로 인증번호를 전송합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH404",
                    description = "User not found."
            )
    })
    @PostMapping("/update/password/email")
    public ApiResponse<EmailResponse> sendPasswordEmail(@RequestBody @Valid EmailRequest emailRequest) {
        EmailResponse emailResponse = emailQueryService.sendPasswordEmail(emailRequest);
        return ApiResponse.onSuccess(emailResponse);
    }

    @Operation(
            summary = "accessToken 재발급 API | by 지지",
            description = "사용자의 refreshToken을 검증하여 accessToken을 재발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TOKEN401",
                    description = "The token is invalid."
            )
    })
    @PostMapping("/reissue")
    public ApiResponse<String> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        authCommandService.reissueToken(request, response);
        return ApiResponse.onSuccess("토큰 재발급에 성공하였습니다.");
    }

    @Operation(
            summary = "자체 로그인 API | by 지지",
            description = "사용자가 자체 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH401",
                    description = "Incorrect password."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH404",
                    description = "User not found."
            )
    })
    @PostMapping("/login/local")
    public ApiResponse<AuthResponse.LoginResponse> loginLocal(@RequestBody @Valid AuthRequest.LocalRequest authRequest, HttpServletResponse response) {
        AuthResponse.LoginResponse loginResponse = authCommandService.localLogin(authRequest, response);
        return ApiResponse.onSuccess(loginResponse);
    }

    @Operation(
            summary = "회원가입/로그인 후 토큰 확인 용 API | by 지지",
            description = "회원가입/로그인 후 토큰 확인할 수 있습니다. "
    )
    @GetMapping("/check/token")
    public ApiResponse<TokenInfo> checkCookies(HttpServletRequest request) {
        TokenInfo tokenInfo = authConverter.toTokenInfo(request);
        return ApiResponse.onSuccess(tokenInfo);
    }

    @Operation(
            summary = "Kakao Web 소셜 로그인 용 API | by 지지",
            description = "사용자가 카카오 소셜 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "Invalid request."
            )
    })
    @GetMapping("/oauth2/authorization/kakao")
    public void kakaoLogin() {
    }

    @Operation(
            summary = "Google Web 소셜 로그인 용 API | by 지지",
            description = "사용자가 구글 소셜 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "Invalid request."
            )
    })
    @GetMapping("/oauth2/authorization/google")
    public void googleLogin() {
    }


    @Operation(
            summary = "Github Web 소셜 로그인 용 API | by 지지",
            description = "사용자가 github 소셜 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "Invalid request."
            )
    })
    @GetMapping("/oauth2/authorization/github")
    public void githubLogin() {
    }

}