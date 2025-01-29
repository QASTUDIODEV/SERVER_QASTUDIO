package qastudio.backend.global.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import qastudio.backend.domain.auth.service.AuthQueryService;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.JwtTokenProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountTableRepository accountTableRepository;
    private final UserRepository userRepository;
    private final AuthQueryService authQueryService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 유저 정보 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 이메일 추출
        String email = extractEmail(registrationId, oAuth2UserAttributes, userRequest);

        // 사용자 고유 식별자
        String userNameAttributeName = registrationId + "_" + oAuth2UserInfo.getId();

        // Attributes에 사용자 고유 식별자 추가
        Map<String, Object> updatedAttributes = new HashMap<>(oAuth2UserAttributes);
        updatedAttributes.put("registrationId", registrationId);
        updatedAttributes.put("email", email);
        updatedAttributes.put(userNameAttributeName, userNameAttributeName);

        // 현재 로그인된 사용자 확인
        User currentUser = getCurrentAuthenticatedUser();

        if (currentUser != null) {
            return linkOrFail(currentUser, email, oAuth2UserInfo, updatedAttributes);
        } else {
            return getOrSave(updatedAttributes, oAuth2UserInfo, email);
        }
    }

    // 현재 로그인된 사용자 기준으로 계정 추가 (계정 연동)
    private OAuth2User linkOrFail(User user, String email, OAuth2UserInfo oAuth2UserInfo, Map<String, Object> updatedAttributes) {
        if (user == null) {
            updatedAttributes.put("error", "UNAUTHORIZED");
            return buildErrorOAuth2User(updatedAttributes);
        }

        AccountTable existingAccount = accountTableRepository.findByEmailAndEmailType(email, oAuth2UserInfo.getEmailType())
                .orElse(null);

        if (existingAccount != null && !existingAccount.getUser().getId().equals(user.getId())) {

            throw new OAuth2AuthenticationException("ACCOUNT_ALREADY_LINKED_TO_ANOTHER_USER");
        }

        if (existingAccount == null) {
            AccountTable newAccount = AccountTable.builder()
                    .email(email)
                    .emailType(oAuth2UserInfo.getEmailType())
                    .user(user)
                    .build();
            user.addAccount(newAccount);
            accountTableRepository.save(newAccount);
        }

        return buildOAuth2User(updatedAttributes, email, oAuth2UserInfo);
    }

    private OAuth2User buildErrorOAuth2User(Map<String, Object> updatedAttributes) {
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_GUEST")),
                updatedAttributes,
                "error"
        );
    }

    // 기존 계정 검색 및 저장 (기존 사용자 또는 신규 사용자 처리)
    private OAuth2User getOrSave(Map<String, Object> updatedAttributes, OAuth2UserInfo oAuth2UserInfo, String email) {
        List<AccountTable> existingAccounts = accountTableRepository.findByEmail(email);

        if (existingAccounts.isEmpty()) {
            createNewUser(email, oAuth2UserInfo);
            return buildOAuth2User(updatedAttributes, email, oAuth2UserInfo);
        }

        User existingUser = existingAccounts.get(0).getUser();
        return linkOrFail(existingUser, email, oAuth2UserInfo, updatedAttributes);
    }

    // 새 사용자 및 계정 생성
    private User createNewUser(String email, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .nickname("")
                .build();
        userRepository.save(user);

        AccountTable accountTable = AccountTable.builder()
                .email(email)
                .emailType(oAuth2UserInfo.getEmailType())
                .user(user)
                .build();
        user.addAccount(accountTable);
        accountTableRepository.save(accountTable);

        return user;
    }

    // OAuth2User 객체 생성
    private OAuth2User buildOAuth2User(Map<String, Object> updatedAttributes, String email, OAuth2UserInfo oAuth2UserInfo) {
        Map<String, Object> attributes = new HashMap<>(updatedAttributes);
        attributes.put("email", email);
        attributes.put("emailType", oAuth2UserInfo.getEmailType().toString());

        Long userId = authQueryService.findUserIdByEmail(email, oAuth2UserInfo.getEmailType());

        attributes.put("userId", userId);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "userId"
        );
    }

    // 현재 로그인된 사용자 가져오기 (없으면 null 반환)
    public User getCurrentAuthenticatedUser() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            return null;
        }

        String accessToken = authQueryService.getCookieValue(request, "accessToken");
        if (accessToken == null || accessToken.isBlank()) {
            return null;
        }

        Long userId = null;
        try {
            userId = jwtTokenProvider.getUserIdFromToken(accessToken);
        } catch (Exception e) {
            return null;
        }

        if (userId == null) {
            return null;
        }

        return userRepository.findById(userId).orElse(null);
    }

    // ✅ 현재 HTTP 요청을 가져오는 메서드 추가
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attributes != null) ? attributes.getRequest() : null;
    }

    // 이메일 추출
    private String extractEmail(String registrationId, Map<String, Object> attributes, OAuth2UserRequest userRequest) {
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount != null) {
                return (String) kakaoAccount.get("email");
            }
        } else if ("google".equals(registrationId)) {
            return (String) attributes.get("email");
        } else if ("github".equals(registrationId)) {
            String email = (String) attributes.get("email");
            if (email == null) {
                email = fetchGitHubEmail(userRequest);
            }
            return email;
        }
        throw new AuthException(ErrorStatus._BAD_REQUEST);
    }

    // GitHub 이메일 가져오기
    private String fetchGitHubEmail(OAuth2UserRequest userRequest) {
        try {
            // OAuth2UserRequest에서 액세스 토큰 가져오기
            String accessToken = userRequest.getAccessToken().getTokenValue();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getBody() != null && !response.getBody().isEmpty()) {
                for (Map<String, Object> emailObj : response.getBody()) {
                    Boolean isPrimary = (Boolean) emailObj.get("primary");
                    if (isPrimary != null && isPrimary) {
                        return (String) emailObj.get("email");
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch email from GitHub API", e);
        }
        return null;
    }
}