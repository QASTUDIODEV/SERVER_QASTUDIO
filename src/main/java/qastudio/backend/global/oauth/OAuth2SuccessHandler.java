package qastudio.backend.global.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final AccountTableRepository accountTableRepository;
    private static final String URI = "/api/v0/auth/login/success";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Authentication에서 OAuth2User 정보 추출
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        // 사용자 이메일 및 이메일 유형 추출
        String email = (String) principal.getAttributes().get("email");
        String registrationId = (String) principal.getAttributes().get("registrationId");

        // OAuth2UserInfo 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, principal.getAttributes());
        EmailType emailType = oAuth2UserInfo.getEmailType();

        Optional<AccountTable> account = accountTableRepository.findByEmailAndEmailType(email, emailType);

        if (account.isEmpty()) {
            throw new AuthException(ErrorStatus.USER_NOT_FOUND);
        }
        Long userId = account.get().getUser().getId();

        // AccessToken 및 RefreshToken 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(userId, authentication, true);

        // RefreshToken 저장
        customOAuth2UserService.setRefreshToken(email, emailType, tokenInfo.getRefreshToken());

        // Redirect URL 생성
        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("accessToken", tokenInfo.getAccessToken())
                .queryParam("refreshToken", tokenInfo.getRefreshToken())
                .build()
                .toUriString();

        // 클라이언트로 리다이렉트
        response.sendRedirect(redirectUrl);
    }

}
