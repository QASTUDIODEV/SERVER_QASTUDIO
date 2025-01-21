package qastudio.backend.domain.auth.service;

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
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

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
    public  AuthResponse.LoginResponse userSignUp(AuthRequest.LocalRequest request) {
        if (accountTableRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(ErrorStatus.ALREADY_EXIST_EMAIL);
        }

        User user = authConverter.toUser(request);
        userRepository.save(user);

        return authenticateAndGenerateToken(request.getEmail(), request.getPassword());
    }

    @Override
    public AuthResponse.LoginResponse localLogin(AuthRequest.LocalRequest loginRequest) {
        try {
            // 비밀번호 검증 포함
            return authenticateAndGenerateToken(loginRequest.getEmail(), loginRequest.getPassword());
        } catch (AuthException ex) {
            throw new BadRequestException(ErrorStatus.USER_NOT_FOUND);
        } catch (BadCredentialsException ex) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
        }
    }

    // 인증 객체 생성 관련해서 수정 예정
    @Override
    public AuthResponse.LoginResponse authenticateAndGenerateToken(String email, String password) {
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

        return authConverter.toLoginResponse(tokenInfo, user);
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
}

