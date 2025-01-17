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
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AccountTableRepository accountTableRepository;
    private final UserRepository userRepository;
    private final CustomUserDetailsServiceImpl customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthConverter signUpRequestConverter;

    @Override
    public void userSignUp(AuthRequest request) {
        if (existsEmail(request.getEmail())) {
            throw new BadRequestException(ErrorStatus.ALREADY_EXIST_EMAIL);
        }

        var user = signUpRequestConverter.toUser(request);
        userRepository.save(user);
    }

    @Override
    public TokenInfo localLogin(AuthRequest loginRequest) {
        try {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

            // 비밀번호 검증
            if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
            }

            // 인증 객체 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // 사용자 ID 조회
            Long userId = findUserIdByEmailAndEmailType(loginRequest.getEmail(), EmailType.LOCAL);

            // 토큰 생성 및 반환
            return jwtTokenProvider.generateToken(userId, authentication, false);
        } catch (AuthException ex) {
            throw new BadRequestException(ErrorStatus.USER_NOT_FOUND);
        } catch (BadCredentialsException ex) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
        }
    }

    @Transactional(readOnly = true)
    public boolean existsEmail(String email) {
        return accountTableRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Long findUserIdByEmailAndEmailType(String email, EmailType emailType) {
        AccountTable account = accountTableRepository.findByEmailAndEmailType(email, emailType)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
        return account.getUser().getId();
    }
}