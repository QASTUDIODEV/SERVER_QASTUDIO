package qastudio.backend.domain.auth.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.jwt.TokenInfo;

@Component
@RequiredArgsConstructor
public class AuthConverter {

    private final PasswordEncoder passwordEncoder;

    public User toUser(AuthRequest.LocalRequest request) {
        User user = User.builder()
                .nickname("") // 기본 닉네임
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

    public AuthResponse.LoginResponse toLoginResponse(TokenInfo tokenInfo, User user) {
        AuthResponse.LoginResponse loginResponse = AuthResponse.LoginResponse.builder()
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .token(tokenInfo)
                .build();

        return loginResponse;
    }

}