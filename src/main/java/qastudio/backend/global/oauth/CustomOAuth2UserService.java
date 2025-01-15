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
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountTableRepository accountTableRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 유저 정보 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);

        // 유저가 로그인 한 소셜 로그인 계정 받아오기
        String email = extractEmail(registrationId, oAuth2UserAttributes);

        // Custom Principal name 생성
        String userNameAttributeName = registrationId + "_" + oAuth2UserInfo.getId();

        // attribute에 userNameAttributeName 추가
        Map<String, Object> updatedAttributes = new HashMap<>(oAuth2UserAttributes);
        updatedAttributes.put(userNameAttributeName, userNameAttributeName);
        updatedAttributes.put("email", email); // 이메일 추가

        // 회원가입 및 로그인 진행
        User user = getOrSave(oAuth2UserInfo, email);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                updatedAttributes,
                userNameAttributeName
        );
    }

    private User getOrSave(OAuth2UserInfo oAuth2UserInfo, String email) {
        Optional<AccountTable> account = accountTableRepository.findByEmailAndEmailType(email, oAuth2UserInfo.getEmailType());
        User user;

        if (account == null) { // 새로운 유저
            user = User.builder()
                    .nickname("")
                    .build();

            AccountTable accountTable = AccountTable.builder()
                    .emailType(oAuth2UserInfo.getEmailType())
                    .email(email)
                    .user(user)
                    .build();

            user.addAccount(accountTable);
        } else {
            user = account.get().getUser();
        }
        return user;
    }

    @Transactional
    public void setRefreshToken(String email, EmailType emailType, String refreshToken) {
        AccountTable accountTable = accountTableRepository.findByEmailAndEmailType(email, emailType)
                .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_FOUND));

        User user = accountTable.getUser();

        user.updateRefreshToken(refreshToken);
    }

    private String extractEmail(String registrationId, Map<String, Object> attributes) {
        String email = null;

        if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
        } else if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        } else if ("github".equals(registrationId)) {
            email = (String) attributes.get("email");
        }

        if (email == null) {
            throw new IllegalStateException("이메일 정보를 가져올 수 없습니다.");
        }

        return email;
    }
}
