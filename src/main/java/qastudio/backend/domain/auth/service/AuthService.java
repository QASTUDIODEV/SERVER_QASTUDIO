package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.LoginRequest;
import qastudio.backend.domain.auth.dto.request.SignUpRequest;
import qastudio.backend.jwt.TokenInfo;

public interface AuthService {

    // Command 메서드
    void userSignUp(SignUpRequest request);
    TokenInfo localLogin(LoginRequest loginRequest);

    // Query 메서드
    boolean existsEmail(String email);
}