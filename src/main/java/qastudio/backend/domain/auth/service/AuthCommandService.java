package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;

public interface AuthCommandService {
    AuthResponse.LoginResponse userSignUp(AuthRequest.LocalRequest request);
    AuthResponse.LoginResponse localLogin(AuthRequest.LocalRequest loginRequest);
    AuthResponse.LoginResponse authenticateAndGenerateToken(String email, String password);
    void changePassword(AuthRequest.ChangePasswordRequest changePasswordRequest);
}
