package qastudio.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.domain.auth.dto.response.EmailResponse;
import qastudio.backend.domain.auth.service.AuthCommandService;
import qastudio.backend.domain.auth.service.EmailQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.jwt.TokenInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/auth")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final EmailQueryService emailQueryService;

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
                    description = "잘못된 요청입니다."
            )
    })
    @PostMapping("/sign-up")
    public ApiResponse< AuthResponse.LoginResponse> singUpLocal(@RequestBody @Valid AuthRequest.LocalRequest authRequest) {
        AuthResponse.LoginResponse loginResponse = authCommandService.userSignUp(authRequest);
        return ApiResponse.onSuccess(loginResponse);
    }

    @Operation(summary = "자체 회원가입 이메일 인증번호 전송 API | by 지지", description = "자체 회원가입 시, 입력한 이메일로 인증번호를 전송합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH409", description = "이미 등록된 이메일입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EMAIL400", description = "이메일 인증 코드 전송을 실패했습니다.")
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
    public ApiResponse<AuthResponse.LoginResponse> loginLocal(@RequestBody @Valid AuthRequest.LocalRequest authRequest) {
        AuthResponse.LoginResponse loginResponse = authCommandService.localLogin(authRequest);
        return ApiResponse.onSuccess(loginResponse);
    }

    @Operation(
            summary = "Kakao Web 소셜 로그인 용 API",
            description = "사용자가 카카오 소셜 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "잘못된 요청입니다."
            )
    })
    @PostMapping("/login/kakao")
    public RedirectView kakaoLogin() {
        return new RedirectView("/oauth2/authorization/kakao");
    }

    @Operation(
            summary = "Google Web 소셜 로그인 용 API",
            description = "사용자가 구글 소셜 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "잘못된 요청입니다."
            )
    })
    @PostMapping("/login/google")
    public RedirectView googleLogin() {
        return new RedirectView("/oauth2/authorization/google");
    }


    @Operation(
            summary = "Github Web 소셜 로그인 용 API",
            description = "사용자가 github 소셜 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "잘못된 요청입니다."
            )
    })
    @PostMapping("/login/github")
    public RedirectView githubLogin() {
        return new RedirectView("/oauth2/authorization/github");
    }

}
