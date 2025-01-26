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
import qastudio.backend.domain.auth.converter.AuthConverter;
import qastudio.backend.domain.auth.service.AuthCommandService;
import qastudio.backend.domain.auth.service.AuthQueryService;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AccountTableRepository accountTableRepository;
    private final AuthCommandService authCommandService;
    private final AuthConverter authConverter;

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

        boolean isExistingUser = (boolean) principal.getAttributes().get("existing_user");

        User currentUser = userRepository.findByAccountsEmail(email)
                .orElseGet(() -> authCommandService.getOrCreateUser(email, emailType));

        boolean accountExists = accountTableRepository.existsByEmailAndEmailType(email, emailType);
        if (!accountExists) {
            AccountTable newAccount = AccountTable.builder()
                    .email(email)
                    .emailType(emailType)
                    .user(currentUser)
                    .build();
            accountTableRepository.save(newAccount);
        }

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(currentUser.getId(), authentication, true);

        // existing_user, token 정보 전달
        authConverter.setCookie(response, "existing_user", String.valueOf(isExistingUser), 1800);
        authConverter.setCookie(response, "accessToken", tokenInfo.getAccessToken(), 1800); // 30분
        authConverter.setCookie(response, "refreshToken", tokenInfo.getRefreshToken(), 604800); // 1주일

        String redirectUrl = "http://localhost:5173/login/success";
        if (!request.getServerName().contains("localhost")) {
            redirectUrl = "https://dlysp0ocmm6yr.cloudfront.net/login/success";
        }
        response.sendRedirect(redirectUrl);
    }
}