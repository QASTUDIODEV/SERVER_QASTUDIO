package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.jwt.TokenInfo;

public interface AuthCommandService {
    TokenInfo userSignUp(AuthRequest.localLoginReuqest request);
    TokenInfo localLogin(AuthRequest.localLoginReuqest loginRequest);
    TokenInfo authenticateAndGenerateToken(String email, String password);
}