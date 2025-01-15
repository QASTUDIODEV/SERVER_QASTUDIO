package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.auth.converter.AuthConverter;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {

    private final AuthQueryService authQueryService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthConverter signUpRequestConverter;

    // Command 메서드
    @Override
    public void userSignUp(AuthRequest.localLoginReuqest request) {
        if (authQueryService.existsEmail(request.getEmail())) {
            throw new BadRequestException(ErrorStatus.ALREADY_EXIST_EMAIL);
        }

        User user = signUpRequestConverter.toUser(request);
        userRepository.save(user);
    }

    @Override
    public TokenInfo localLogin(AuthRequest.localLoginReuqest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            Long userId = authQueryService.findUserIdByEmailAndEmailType(loginRequest.getEmail(), EmailType.LOCAL);

            return jwtTokenProvider.generateToken(
                    userId,
                    authentication,
                    false // 소셜 로그인 여부
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException(ErrorStatus.INVALID_PASSWORD);
        }
    }
}