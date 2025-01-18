package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.jwt.TokenInfo;

public interface AuthCommandService {
    TokenInfo userSignUp(AuthRequest request);
    TokenInfo localLogin(AuthRequest loginRequest);
    TokenInfo authenticateAndGenerateToken(String email, String password);
}
