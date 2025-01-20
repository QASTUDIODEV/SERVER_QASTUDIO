package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.jwt.TokenInfo;

public interface AuthCommandService {
    TokenInfo userSignUp(AuthRequest.LocalRequest request);
    TokenInfo localLogin(AuthRequest.LocalRequest loginRequest);
    TokenInfo authenticateAndGenerateToken(String email, String password);
    void changePassword(AuthRequest.ChangePasswordRequest changePasswordRequest);
}
