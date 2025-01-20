package qastudio.backend.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
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
            summary = "User 자체 회원가입 API | by 지지",
            description = "사용자가 자체 회원가입을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "회원가입에 성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH409",
                    description = "이미 등록된 이메일입니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON400",
                    description = "잘못된 요청입니다."
            )
    })
    @PostMapping("/sign-up")
    public ApiResponse<Void> UserSignUp(@RequestBody @Valid AuthRequest.localLoginReuqest authRequest) {
        authCommandService.userSignUp(authRequest);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "이메일 인증번호 전송 API | by 지지", description = "자체 회원가입 시, 입력한 이메일로 인증번호를 전송합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH409", description = "이미 등록된 이메일입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "EMAIL400", description = "이메일 인증 코드 전송을 실패했습니다.")
    })
    @PostMapping("/sign-up/email")
    public ApiResponse<EmailResponse> mailConfirm(@RequestBody @Valid EmailRequest emailRequest){
        EmailResponse emailResponse = emailQueryService.sendEmail(emailRequest);
        return ApiResponse.onSuccess(emailResponse);
    }

    @Operation(
            summary = "User 자체 로그인 API | by 지지",
            description = "사용자가 자체 로그인을 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "로그인에 성공했습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH401",
                    description = "비밀번호가 잘못되었습니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "AUTH404",
                    description = "존재하지 않는 사용자입니다."
            )
    })
    @PostMapping("/login/local")
    public ApiResponse<TokenInfo> LocalLogin(@RequestBody @Valid AuthRequest.localLoginReuqest authRequest) {
        TokenInfo loginResponse = authCommandService.localLogin(authRequest);
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
