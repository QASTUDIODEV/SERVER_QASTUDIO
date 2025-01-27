package qastudio.backend.domain.auth.dto.response;

import lombok.*;
import qastudio.backend.global.security.jwt.TokenInfo;

public class AuthResponse {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LoginResponse {
        private String nickname;
    }
}
