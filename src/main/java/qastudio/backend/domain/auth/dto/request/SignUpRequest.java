package qastudio.backend.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import org.springframework.security.crypto.password.PasswordEncoder;

public record SignUpRequest(
        @Email(message = "이메일 형식에 맞지 않습니다.")
        @NotBlank(message = "이메일은 필수 입력 값입니다.") String email,

        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{10,}$|^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=?]).{8,}$",
                message = "영문+숫자 10자 이상 또는 영문+숫자+특수기호 8자 이상을 입력해 주세요.") String password
) {

    public User toEntity(PasswordEncoder passwordEncoder) {
        User user = User.builder()
                .nickname("") // 기본 닉네임
                .profileImage("") // 기본 프로필 이미지
                .bannerImage("") // 기본 배너 이미지
                .build();

        AccountTable accountTable = AccountTable.builder()
                .emailType(EmailType.LOCAL)
                .email(this.email)
                .password(passwordEncoder.encode(this.password)) // 비밀번호 암호화
                .user(user)
                .build();

        user.addAccount(accountTable);

        return user;
    }
}