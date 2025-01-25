package qastudio.backend.global.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.auth.service.AuthCommandService;
import qastudio.backend.domain.auth.service.AuthQueryService;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountTableRepository accountTableRepository;
    private final AuthCommandService authCommandService;
    private final AuthQueryService authQueryService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        String registrationId = (String) principal.getAttributes().get("registrationId");

        if (email == null || registrationId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "email 또는 registrationId가 null입니다.");
            return;
        }

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, principal.getAttributes());
        EmailType emailType = oAuth2UserInfo.getEmailType();

        User currentUser = authQueryService.getAuthenticatedUserIfPresent()
                .orElseGet(() -> authCommandService.getOrCreateUser(email, emailType));

        // 중복 계정 확인 및 추가
        boolean accountExists = accountTableRepository.findByEmailAndEmailType(email, emailType).isPresent();
        if (!accountExists) {
            AccountTable newAccount = AccountTable.builder()
                    .email(email)
                    .emailType(emailType)
                    .user(currentUser)
                    .build();
            currentUser.addAccount(newAccount);
            accountTableRepository.save(newAccount);
        }

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(currentUser.getId(), authentication, true);

        // existing_user, token 정보 전달
        setCookie(response, "existing_user", String.valueOf(accountExists), 1800);
        setCookie(response, "accessToken", tokenInfo.getAccessToken(), 1800); // 30분
        setCookie(response, "refreshToken", tokenInfo.getRefreshToken(), 604800); // 1주일

        String redirectUrl = "http://localhost:5173/login/success";
        if (!request.getServerName().contains("localhost")) {
            redirectUrl = "https://dlysp0ocmm6yr.cloudfront.net/login/success";
        }
        response.sendRedirect(redirectUrl);    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(false); // HTTP-Only 설정 (보안 강화 필요 시 true로 변경하기)
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}