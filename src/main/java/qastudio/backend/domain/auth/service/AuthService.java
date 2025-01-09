package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.jwt.TokenInfo;

public interface AuthService {

    // Command 메서드
    void userSignUp(AuthRequest request);
    TokenInfo localLogin(AuthRequest loginRequest);

    // Query 메서드
    boolean existsEmail(String email);
    Long findUserIdByEmailAndEmailType(String email, EmailType emailType);
}