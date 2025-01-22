package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;

public interface AuthCommandService {
    AuthResponse.LoginResponse userSignUp(AuthRequest.LocalRequest request);
    AuthResponse.LoginResponse localLogin(AuthRequest.LocalRequest loginRequest);
    AuthResponse.LoginResponse authenticateAndGenerateToken(String email, String password);
    void changePassword(AuthRequest.ChangePasswordRequest changePasswordRequest);
    User getOrCreateUser(String email, EmailType emailType);
}
