package qastudio.backend.domain.auth.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;

@Component
@RequiredArgsConstructor
public class AuthConverter {

    private final PasswordEncoder passwordEncoder;

    public User toUser(AuthRequest request) {
        User user = User.builder()
                .nickname("") // 기본 닉네임
                .profileImage("") // 기본 프로필 이미지
                .bannerImage("") // 기본 배너 이미지
                .build();

        AccountTable accountTable = AccountTable.builder()
                .emailType(EmailType.LOCAL)
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .user(user)
                .build();

        user.addAccount(accountTable);

        return user;
    }
}