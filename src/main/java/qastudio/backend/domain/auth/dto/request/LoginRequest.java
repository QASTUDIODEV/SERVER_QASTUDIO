package qastudio.backend.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.") String email,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.") String password
) {
}
