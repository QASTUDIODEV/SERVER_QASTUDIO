package qastudio.backend.global.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Transactional
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountTableRepository accountTableRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 유저 정보 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        // OAuth2 공급자 ID
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2UserInfo 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 이메일 추출
        String email = extractEmail(registrationId, oAuth2UserAttributes);

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

    private User getOrSave(OAuth2UserInfo oAuth2UserInfo, String email) {
        return accountTableRepository.findByEmailAndEmailType(email, oAuth2UserInfo.getEmailType())
                .map(AccountTable::getUser)
                .orElseGet(() -> createUser(oAuth2UserInfo, email));
    }

    private User createUser(OAuth2UserInfo oAuth2UserInfo, String email) {
        User user = User.builder()
                .nickname("")
                .build();

        AccountTable accountTable = AccountTable.builder()
                .email(email)
                .emailType(oAuth2UserInfo.getEmailType())
                .user(user)
                .build();

        user.addAccount(accountTable);
        return user;
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return (String) kakaoAccount.get("email");
        } else if ("google".equals(registrationId) || "github".equals(registrationId)) {
            return (String) attributes.get("email");
        }
        throw new AuthException(ErrorStatus._BAD_REQUEST);
    }
}