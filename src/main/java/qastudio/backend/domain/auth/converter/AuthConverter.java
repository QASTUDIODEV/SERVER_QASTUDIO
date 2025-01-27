package qastudio.backend.domain.auth.converter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.domain.auth.service.AuthQueryService;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.TokenException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.TokenInfo;

@Component
@RequiredArgsConstructor
public class AuthConverter {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthQueryService authQueryService;
    private final AccountTableRepository accountTableRepository;

    public User toUser() {
        User newUser = User.builder().nickname("").build();
        userRepository.save(newUser);
        return newUser;
    }

    public User toUser(String email, EmailType emailType) {
        User newUser = User.builder()
                .nickname("")
                .build();
        userRepository.save(newUser);

        AccountTable newAccount = AccountTable.builder()
                .email(email)
                .emailType(emailType)
                .user(newUser)
                .build();
        accountTableRepository.save(newAccount);

        return newUser;
    }

    public User toUserAccountTable(AuthRequest.LocalRequest request) {
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

    public AccountTable toAccountTable(String email, String password, User user) {
        AccountTable accountTable = AccountTable.builder()
                .emailType(EmailType.LOCAL)
                .email(email)
                .password(passwordEncoder.encode(password))
                .user(user)
                .build();

        accountTableRepository.save(accountTable);

        user.addAccount(accountTable);

        return accountTable;
    }

    public AuthResponse.LoginResponse toLoginResponse(User user) {
        AuthResponse.LoginResponse loginResponse = AuthResponse.LoginResponse.builder()
                .nickname(user.getNickname())
                .build();

        return loginResponse;
    }

    public TokenInfo toTokenInfo(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new TokenException(ErrorStatus.NULL_TOKEN);
        }

        String accessToken = authQueryService.getCookieValue(request, "accessToken");
        String refreshToken = authQueryService.getCookieValue(request, "refreshToken");

        if (accessToken == null || refreshToken == null) {
            throw new TokenException(ErrorStatus.NULL_TOKEN);
        }

        return new TokenInfo("Bearer", accessToken, refreshToken);
    }

    public Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(maxAge);

        cookie.setAttribute("SameSite", "None");

        return cookie;
    }

}