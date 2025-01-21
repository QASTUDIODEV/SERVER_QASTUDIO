package qastudio.backend.global.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

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

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 유저 정보 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // OAuth2 공급자 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2UserInfo 생성
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

        getOrSave(oAuth2UserInfo, email);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                updatedAttributes,
                userNameAttributeName
        );
    }

    // 사용자 조회 또는 저장
    private User getOrSave(OAuth2UserInfo oAuth2UserInfo, String email) {
        return accountTableRepository.findByEmailAndEmailType(email, oAuth2UserInfo.getEmailType())
                .map(AccountTable::getUser)
                .orElseGet(() -> createUser(oAuth2UserInfo, email));
    }

    // 사용자 생성
    private User createUser(OAuth2UserInfo oAuth2UserInfo, String email) {
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

    // GitHub API를 통해 이메일 가져오기
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