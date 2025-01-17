package qastudio.backend.global.oauth;

import lombok.Builder;
import lombok.Getter;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.Map;

@Builder
@Getter
public class OAuth2UserInfo {

    private final String id;
    private final EmailType emailType;

    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "kakao" -> ofKakao(attributes);
            default -> throw new AuthException(ErrorStatus.ILLEGAL_REGISTRATION_ID);
        };
    }

    // Kakao 사용자 정보 생성
    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
        Object idObj = attributes.get("id");

        if (idObj == null) {
            throw new AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE);
        }

        return OAuth2UserInfo.builder()
                .id(String.valueOf(idObj))
                .emailType(EmailType.KAKAO)
                .build();
    }
}