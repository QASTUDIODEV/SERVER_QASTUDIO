package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.auth.dto.request.SignUpRequest;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.AccountTableRepository;
import qastudio.backend.domain.user.repository.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;

import static qastudio.backend.global.apiPayload.code.status.ErrorStatus.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final AccountTableRepository accountTableRepository;
    private final UserRepository userRepository;

    public void userSignUp(SignUpRequest request) {
        if(accountTableRepository.existsByEmail(request.email())) {
            throw new BadRequestException(ALREADY_EXIST_EMAIL);
        }

        User user = request.toEntity(passwordEncoder);
        userRepository.save(user);
    }
}
