package qastudio.backend.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.auth.converter.AuthConverter;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.JwtTokenProvider;
import qastudio.backend.global.security.jwt.TokenInfo;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountTableRepository accountTableRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthQueryService authQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthConverter authConverter;

    @Override
    public void userSignUp(AuthRequest.LocalRequest request, HttpServletResponse response) {
            String email = request.getEmail();
            EmailType emailType = EmailType.LOCAL;

            List<AccountTable> accountTables = accountTableRepository.findByEmail(email);
            boolean accountExists = accountTables.stream().anyMatch(account -> account.getEmailType().equals(emailType));

            if (accountExists) {
                throw new AuthException(ErrorStatus.ALREADY_EXIST_EMAIL);
            }

            User user;
            if (accountTables.isEmpty()) {
                user = authConverter.toUserAccountTable(request);
                userRepository.save(user);
            } else {
                user = accountTables.get(0).getUser();
                authConverter.toAccountTable(email, request.getPassword(), user);
            }

            TokenInfo tokenInfo = authenticateAndGenerateToken(email, request.getPassword());

            AuthResponse.LoginResponse loginResponse = authConverter.toLoginResponse(tokenInfo, false);

            Cookie accessToken_cookie = authConverter.createCookie("accessToken", loginResponse.getToken().getAccessToken(), 1800);
            Cookie refreshToken_cookie = authConverter.createCookie("refreshToken", loginResponse.getToken().getRefreshToken(), 604800);

            response.addCookie(accessToken_cookie);
            response.addCookie(refreshToken_cookie);
    }


    @Override
    public void localLogin(AuthRequest.LocalRequest loginRequest, HttpServletResponse response) {
        try {
            TokenInfo tokenInfo = authenticateAndGenerateToken(loginRequest.getEmail(), loginRequest.getPassword());

            Cookie existing_user_cookie = authConverter.createCookie("existing_user", "true", 1800);
            Cookie accessToken_cookie = authConverter.createCookie("accessToken", tokenInfo.getAccessToken(), 1800);
            Cookie refreshToken_cookie = authConverter.createCookie("refreshToken", tokenInfo.getRefreshToken(), 604800);

            response.addCookie(existing_user_cookie);
            response.addCookie(accessToken_cookie);
            response.addCookie(refreshToken_cookie);

        } catch (AuthException ex) {
            throw new BadRequestException(ErrorStatus.USER_NOT_FOUND);
        } catch (BadCredentialsException ex) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
        }
    }

    // 인증 객체 생성 관련해서 수정 예정
    @Override
    public TokenInfo authenticateAndGenerateToken(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        User user = authQueryService.findUserIdByEmailAndEmailType(email, EmailType.LOCAL);
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(user.getId(), authentication, false);

        return tokenInfo;
    }

    @Override
    public void changePassword(AuthRequest.ChangePasswordRequest changePasswordRequest) {
        Optional<AccountTable> accountTable = accountTableRepository.findByEmailAndEmailType(changePasswordRequest.getEmail(), EmailType.LOCAL);

        if (accountTable.isEmpty()) {
            throw new AuthException(ErrorStatus.USER_NOT_FOUND);
        }

        AccountTable account = accountTable.get();

        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), account.getPassword())) {
            throw new AuthException(ErrorStatus.PASSWORD_ALREADY_USED);
        }

        String encodedNewPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        account.updatePassword(encodedNewPassword);

        accountTableRepository.save(account);
    }

    @Override
    public User getOrCreateUser(String email, EmailType emailType) {
        Optional<AccountTable> existingAccount = accountTableRepository.findByEmailAndEmailType(email, emailType);
        return existingAccount.map(AccountTable::getUser).orElseGet(() -> {
            User newUser = authConverter.toUser();
            return newUser;
        });
    }
}

