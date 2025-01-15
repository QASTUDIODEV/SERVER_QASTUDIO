package qastudio.backend.domain.auth.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qastudio.backend.domain.user.entity.enums.EmailType;

public class AuthRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class localLoginReuqest {
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class socialLoginReuqest {
        @NotBlank
        @Enumerated(EnumType.STRING)
        private EmailType emailType;

        @NotBlank
        private String token;
    }
}
