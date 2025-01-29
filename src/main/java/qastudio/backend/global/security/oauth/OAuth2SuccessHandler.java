package qastudio.backend.global.security.oauth;

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
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.JwtTokenProvider;
import qastudio.backend.global.security.jwt.TokenInfo;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final AccountTableRepository accountTableRepository;
    private final AuthQueryService authQueryService;
    private final AuthConverter authConverter;

    private static final String REDIRECT_URL = "http://localhost:3000/login/success";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = (String) principal.getAttributes().get("email");
        String registrationId = (String) principal.getAttributes().get("registrationId");

        if (email == null || registrationId == null) {
            redirectWithError(response, "Email or registrationId is missing.");
            return;
        }

        EmailType emailType = EmailType.valueOf(registrationId.toUpperCase());
        String addSocial = request.getParameter("addSocial");

        // ✅ accessToken에서 userId 추출
        String accessToken = getAccessTokenFromRequest(request);
        Long userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        if (userId != null) {
            // 로그인된 사용자가 있는 경우
            handleAddSocialOrRedirect(response, userId, email, emailType, addSocial);
        } else {
            // 로그인된 사용자가 없는 경우
            handleNewUser(response, email, emailType);
        }
    }

    // ✅ accessToken 추출
    private String getAccessTokenFromRequest(HttpServletRequest request) {
        return authQueryService.getCookieValue(request, "accessToken");
    }

    // ✅ 추가 소셜 계정 연결 또는 로그인 상태 확인
    private void handleAddSocialOrRedirect(HttpServletResponse response, Long userId, String email,
                                           EmailType emailType, String addSocial) throws IOException {
        Optional<User> optionalUser = userRepository.findById(userId);

        User currentUser;

        if (optionalUser.isPresent()) {
            currentUser = optionalUser.get();
            log.info("✅ Found user: {}", currentUser.getId());
        } else {
            throw new AuthException(ErrorStatus.USER_NOT_FOUND);
        }
        AccountTable existingAccount = accountTableRepository.findByEmailAndEmailType(email, emailType).orElse(null);

        if ("true".equals(addSocial)) {
            // 계정 추가 처리
            if (existingAccount != null && !existingAccount.getUser().getId().equals(currentUser.getId())) {
                redirectWithError(response, "Social account is already linked to another user.");
                return;
            }

            if (existingAccount == null) {
                AccountTable newAccount = AccountTable.builder()
                        .email(email)
                        .emailType(emailType)
                        .user(currentUser)
                        .build();
                accountTableRepository.save(newAccount);
            }
            redirectWithSuccess(response, "Social account added successfully.");
        } else {
            generateAndRedirect(response, currentUser);
        }
    }

    // ✅ 새 사용자 처리
    private void handleNewUser(HttpServletResponse response, String email, EmailType emailType) throws IOException {
        AccountTable existingAccount = accountTableRepository.findByEmailAndEmailType(email, emailType).orElse(null);

        User currentUser = (existingAccount != null) ? existingAccount.getUser() :
                authConverter.toUserAccountTable(email, emailType);

        generateAndRedirect(response, currentUser);
    }

    // ✅ JWT 생성 및 성공 리다이렉트
    private void generateAndRedirect(HttpServletResponse response, User user) throws IOException {
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(user.getId(), null);
        response.addCookie(authConverter.createCookie("accessToken", tokenInfo.getAccessToken(), 1800));
        response.addCookie(authConverter.createCookie("refreshToken", tokenInfo.getRefreshToken(), 604800));
        redirectWithSuccess(response, null);
    }

    // ✅ 성공 리다이렉트
    private void redirectWithSuccess(HttpServletResponse response, String message) throws IOException {
        String url = REDIRECT_URL + "?status=success";
        if (message != null) {
            url += "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        }
        response.sendRedirect(url);
    }

    // ✅ 에러 리다이렉트
    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        response.sendRedirect(REDIRECT_URL + "?status=error&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8));
    }
}