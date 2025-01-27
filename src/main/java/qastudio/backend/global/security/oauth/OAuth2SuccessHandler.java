package qastudio.backend.global.security.oauth;

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
import qastudio.backend.domain.auth.converter.AuthConverter;
import qastudio.backend.domain.auth.service.AuthQueryService;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.global.security.jwt.JwtTokenProvider;
import qastudio.backend.global.security.jwt.TokenInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountTableRepository accountTableRepository;
    private final AuthQueryService authQueryService;
    private final AuthConverter authConverter;

    private static final String REDIRECT_URL = "https://localhost:5173/login/success";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        String registrationId = (String) principal.getAttributes().get("registrationId");

        if (email == null || registrationId == null) {
            response.sendRedirect(REDIRECT_URL + "?status=error&message=" +
                    URLEncoder.encode("Email or registrationId is missing.", StandardCharsets.UTF_8));
            return;
        }

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, principal.getAttributes());
        EmailType emailType = oAuth2UserInfo.getEmailType();

        // 이메일 기반으로 기존 계정 확인
        AccountTable existingAccount = accountTableRepository.findByEmailAndEmailType(email, emailType)
                .orElse(null);

        User currentUser = authQueryService.getAuthenticatedUserIfPresent().orElse(null);

        if (currentUser == null) {
            // 현재 인증된 사용자가 없으면 기존 계정을 확인 후 사용
            if (existingAccount != null) {
                currentUser = existingAccount.getUser();
            } else {
                // 사용자가 없으면 새 사용자 생성
                currentUser = authConverter.toUser(email, emailType);
            }
        } else {
            // 로그인된 사용자가 있지만 다른 계정과 연결된 경우
            if (existingAccount != null && !existingAccount.getUser().getId().equals(currentUser.getId())) {
                response.sendRedirect(REDIRECT_URL + "?status=error&code=AUTH411&message=" +
                        URLEncoder.encode("Social account linking is not allowed.", StandardCharsets.UTF_8));
                return;
            }

            if (existingAccount == null) {
                // 계정이 존재하지 않으면 새로운 계정 추가
                AccountTable newAccount = AccountTable.builder()
                        .email(email)
                        .emailType(emailType)
                        .user(currentUser)
                        .build();

                currentUser.addAccount(newAccount);
                accountTableRepository.save(newAccount);
            }
        }

        // JWT 토큰 발급
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(currentUser.getId(), authentication, true);

        response.addCookie(authConverter.createCookie("accessToken", tokenInfo.getAccessToken(), 1800));
        response.addCookie(authConverter.createCookie("refreshToken", tokenInfo.getRefreshToken(), 604800));

        response.sendRedirect(REDIRECT_URL + "?status=success");
    }
}