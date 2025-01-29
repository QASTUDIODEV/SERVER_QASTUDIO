package qastudio.backend.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

public class AuthRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocalRequest {
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangePasswordRequest {
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String newPassword;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddSocialRequest {
        @NotBlank(message = "소셜 타입은 필수 입력 값입니다.")
        private String emailType;

        public EmailType getEmailType() {
            try {
                return EmailType.valueOf(emailType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AuthException(ErrorStatus.UNSUPPORTED_SOCIAL_TYPE);
            }
        }
    }
}
