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
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
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
public class AuthCommandServiceImpl implements AuthCommandService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthQueryService authQueryService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthConverter signUpRequestConverter;

    @Override
    public TokenInfo userSignUp(AuthRequest.localLoginReuqest request) {
        if (authQueryService.existsEmail(request.getEmail())) {
            throw new BadRequestException(ErrorStatus.ALREADY_EXIST_EMAIL);
        }

        User user = signUpRequestConverter.toUser(request);
        userRepository.save(user);

        return authenticateAndGenerateToken(request.getEmail(), request.getPassword());
    }

    @Override
    public TokenInfo localLogin(AuthRequest.localLoginReuqest loginRequest) {
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
    public TokenInfo authenticateAndGenerateToken(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
        }

        // 인증 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // 사용자 ID 조회
        Long userId = authQueryService.findUserIdByEmailAndEmailType(email, EmailType.LOCAL);

        // 토큰 생성 및 반환
        return jwtTokenProvider.generateToken(userId, authentication, false);
    }
}