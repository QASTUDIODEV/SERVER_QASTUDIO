package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.auth.dto.request.LoginRequest;
import qastudio.backend.domain.auth.dto.request.SignUpRequest;
import qastudio.backend.domain.auth.dto.response.LoginResponse;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTableRepository;
import qastudio.backend.domain.user.repository.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.jwt.JwtTokenProvider;
import qastudio.backend.jwt.TokenInfo;

import static qastudio.backend.global.apiPayload.code.status.ErrorStatus.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AccountTableRepository accountTableRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    public void userSignUp(SignUpRequest request) {
        if(accountTableRepository.existsByEmail(request.email())) {
            throw new BadRequestException(ALREADY_EXIST_EMAIL);
        }

        User user = request.toEntity(passwordEncoder);
        userRepository.save(user);
    }

    public TokenInfo localLogin(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            return jwtTokenProvider.generateToken(
                    loginRequest.email(),
                    authentication,
                    false // 소셜 로그인 여부
            );
        } catch (BadCredentialsException ex) {
            throw new BadRequestException(INVALID_PASSWORD);
        }
    }
}
