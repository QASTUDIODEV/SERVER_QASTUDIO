package qastudio.backend.global.security.oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.Map;

@Builder
@Getter
@Slf4j
public class OAuth2UserInfo {

    // 사용자 id, email, 소셜 타입
    private final String id;
    private final String email;
    private final EmailType emailType;

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> ofKakao(attributes);
            case "google" -> ofGoogle(attributes);
            case "github" -> ofGithub(attributes);
            default -> throw new AuthException(ErrorStatus.ILLEGAL_REGISTRATION_ID);
        };
    }

    // Kakao 사용자 정보 생성
    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Object idObj = attributes.get("id");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (idObj == null) {
            throw new AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE);
        }

        String email = (String) kakaoAccount.get("email");
        if (email == null) {
            throw new AuthException(ErrorStatus.EMAIL_NOT_FOUND);
        }

        return OAuth2UserInfo.builder()
                .id(String.valueOf(idObj))
                .email(email)
                .emailType(EmailType.KAKAO)
                .build();
    }

    // Google 사용자 정보 생성
    private static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        Object idObj = attributes.get("sub");
        String email = (String) attributes.get("email");

        if (idObj == null || email == null) {
            throw new AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE);
        }

        return OAuth2UserInfo.builder()
                .id(String.valueOf(idObj))
                .email(email)
                .emailType(EmailType.GOOGLE)
                .build();
    }

    // GitHub 사용자 정보 생성
    private static OAuth2UserInfo ofGithub(Map<String, Object> attributes) {
        Object idObj = attributes.get("id");
        Object loginObj = attributes.get("login");
        String email = (String) attributes.get("email");

        if (idObj == null || loginObj == null) {
            throw new AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE);
        }

        return OAuth2UserInfo.builder()
                .id(String.valueOf(idObj))
                .email(email)
                .emailType(EmailType.GITHUB)
                .build();
    }
}