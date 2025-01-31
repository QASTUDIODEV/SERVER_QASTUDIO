package qastudio.backend.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import qastudio.backend.global.apiPayload.code.exception.custom.TokenException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.security.jwt.JwtTokenProvider;
import qastudio.backend.global.security.jwt.TokenInfo;

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
    private final StringRedisTemplate redisTemplate;

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

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getId().toString(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
             );


        TokenInfo tokenInfo = jwtTokenProvider.generateToken(user.getId(), authentication);

            Cookie accessToken_cookie = authConverter.createCookie("accessToken", tokenInfo.getAccessToken(), 1800);
            Cookie refreshToken_cookie = authConverter.createCookie("refreshToken", tokenInfo.getRefreshToken(), 604800);

            response.addCookie(accessToken_cookie);
            response.addCookie(refreshToken_cookie);
    }


    @Override
    public AuthResponse.LoginResponse localLogin(AuthRequest.LocalRequest loginRequest, HttpServletResponse response) {
        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

            if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
            }

            User user = authQueryService.findUserIdByEmailAndEmailType(loginRequest.getEmail(), EmailType.LOCAL);

            redisTemplate.delete("logout:" + user.getId());

            TokenInfo tokenInfo = jwtTokenProvider.generateToken(user.getId(), null);

            Cookie accessToken_cookie = authConverter.createCookie("accessToken", tokenInfo.getAccessToken(), 1800);
            Cookie refreshToken_cookie = authConverter.createCookie("refreshToken", tokenInfo.getRefreshToken(), 604800);
            response.addCookie(accessToken_cookie);
            response.addCookie(refreshToken_cookie);

            return authConverter.toLoginResponse(user);

        } catch (AuthException ex) {
            throw new BadRequestException(ErrorStatus.USER_NOT_FOUND);
        } catch (BadCredentialsException ex) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
        }
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
    public void reissueToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authQueryService.getCookieValue(request, "refreshToken");

        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenException(ErrorStatus.NULL_TOKEN);
        }

        TokenInfo newTokenInfo = jwtTokenProvider.reissueToken(refreshToken);

        Cookie accessToken_cookie = authConverter.createCookie("accessToken", newTokenInfo.getAccessToken(), 1800);
        Cookie refreshToken_cookie = authConverter.createCookie("refreshToken", newTokenInfo.getRefreshToken(), 604800);

        response.addCookie(accessToken_cookie);
        response.addCookie(refreshToken_cookie);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = authQueryService.getCookieValue(request, "refreshToken");

        if(refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenException(ErrorStatus.NULL_TOKEN);
        }

        jwtTokenProvider.logout(refreshToken);

        // 쿠키 삭제
        Cookie accessTokenCookie = authConverter.createCookie("accessToken", "", 0);
        Cookie refreshTokenCookie = authConverter.createCookie("refreshToken", "", 0);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }


}

