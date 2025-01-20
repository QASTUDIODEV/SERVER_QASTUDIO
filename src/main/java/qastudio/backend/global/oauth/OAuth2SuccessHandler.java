package qastudio.backend.global.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountTableRepository accountTableRepository;
    private static final String URI = "/api/v0/auth/login/success";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 authentication success triggered.");

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        log.info("Principal attributes: {}", principal.getAttributes());

        // 소셜 로그인 시 필요한 사용자 정보
        String email = (String) principal.getAttributes().get("email");
        String registrationId = (String) principal.getAttributes().get("registrationId");

        log.info("Extracted email: {}, registrationId: {}", email, registrationId);

        if (email == null || registrationId == null) {
            log.error("Missing email or registrationId in OAuth2 attributes.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "email 또는 registrationId가 null입니다.");
            return;
        }

        // OAuth2UserInfo 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, principal.getAttributes());
        EmailType emailType = oAuth2UserInfo.getEmailType();

        log.info("User info extracted - EmailType: {}", emailType);

        log.info("Fetching user with email: {} and emailType: {}", email, emailType);
        Optional<AccountTable> accountOptional = accountTableRepository.findByEmailAndEmailType(email, emailType);

        if (accountOptional.isEmpty()) {
            log.error("User not found for email: {}, emailType: {}", email, emailType);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
            return;
        }

        AccountTable account = accountOptional.get();
        Long userId = account.getUser().getId();

        log.info("User found with ID: {}", userId);

        // accessToken, refreshToken 발급
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(userId, authentication, true);
        log.info("Generated tokens - AccessToken: {}, RefreshToken: {}", tokenInfo.getAccessToken(), tokenInfo.getRefreshToken());

        // 토큰 전달 Redirect URL
        String redirectUrl = UriComponentsBuilder.fromUriString(URI)
                .queryParam("type", "Bearer")
                .queryParam("accessToken", tokenInfo.getAccessToken())
                .queryParam("refreshToken", tokenInfo.getRefreshToken())
                .build()
                .toUriString();

        log.info("Redirecting to: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}