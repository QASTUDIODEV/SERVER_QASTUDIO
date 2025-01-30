package qastudio.backend.global.security.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.auth.converter.AuthConverter;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.JwtTokenProvider;
import qastudio.backend.global.security.jwt.TokenInfo;
import qastudio.backend.global.security.oauth.CustomOAuth2UserService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountTableRepository accountTableRepository;
    private final AuthConverter authConverter;
    private final CustomOAuth2UserService customOAuth2UserService;

    private static final String REDIRECT_URL = "https://localhost:5173/login/success";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User principal = (OAuth2User) authentication.getPrincipal();
            String email = (String) principal.getAttributes().get("email");
            String registrationId = (String) principal.getAttributes().get("registrationId");

            if (email == null || registrationId == null) {
                redirectWithError(response, "Email or registrationId is missing.");
                return;
            }

            EmailType emailType = EmailType.valueOf(registrationId.toUpperCase());
            String addSocial = request.getParameter("addSocial");

            User currentUser = customOAuth2UserService.getCurrentAuthenticatedUser();

            if (currentUser != null) {
                handleAddSocialOrRedirect(response, currentUser, email, emailType, addSocial);
            } else {
                handleNewUser(response, email, emailType);
            }
        } catch (OAuth2AuthenticationException e) {
            redirectWithError(response, e.getMessage());
        } catch (Exception e) {
            redirectWithError(response, "Unexpected authentication error.");
        }
    }

    // 추가 소셜 계정 연결 또는 로그인 상태 확인
    private void handleAddSocialOrRedirect(HttpServletResponse response, User currentUser, String email,
                                           EmailType emailType, String addSocial) throws IOException {
        try {
            AccountTable existingAccount = accountTableRepository.findByEmailAndEmailType(email, emailType).orElse(null);

            if ("true".equals(addSocial)) {
                if (existingAccount != null && !existingAccount.getUser().getId().equals(currentUser.getId())) {
                    throw new AuthException(ErrorStatus.ACCOUNT_ALREADY_LINKED_TO_ANOTHER_USER);
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
        } catch (AuthException e) {
            redirectWithError(response, e.getMessage());
        }
    }

    // 새 사용자 처리
    private void handleNewUser(HttpServletResponse response, String email, EmailType emailType) throws IOException {
        AccountTable existingAccount = accountTableRepository.findByEmailAndEmailType(email, emailType).orElse(null);

        User currentUser = (existingAccount != null) ? existingAccount.getUser() :
                authConverter.toUserAccountTable(email, emailType);

        generateAndRedirect(response, currentUser);
    }

    // JWT 생성 및 성공 리다이렉트
    private void generateAndRedirect(HttpServletResponse response, User user) throws IOException {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getId().toString())
                .password("")
                .roles("USER")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(user.getId(), authentication);

        response.addCookie(authConverter.createCookie("accessToken", tokenInfo.getAccessToken(), 1800));
        response.addCookie(authConverter.createCookie("refreshToken", tokenInfo.getRefreshToken(), 604800));
        redirectWithSuccess(response, null);
    }

    // 성공 리다이렉트
    private void redirectWithSuccess(HttpServletResponse response, String message) throws IOException {
        String url = REDIRECT_URL + "?status=success";
        if (message != null) {
            url += "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        }
        response.sendRedirect(url);
    }

    // 에러 리다이렉트
    private void redirectWithError(HttpServletResponse response, String message) throws IOException {
        String url = REDIRECT_URL + "?status=error";
        if (message != null) {
            url += "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
        }
        response.sendRedirect(url);
    }
}