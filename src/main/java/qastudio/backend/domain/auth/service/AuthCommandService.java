package qastudio.backend.domain.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.global.security.jwt.TokenInfo;

import java.io.IOException;

public interface AuthCommandService {
    void userSignUp(AuthRequest.LocalRequest request, HttpServletResponse response);
    void localLogin(AuthRequest.LocalRequest loginRequest, HttpServletResponse response);
    TokenInfo authenticateAndGenerateToken(String email, String password);
    void changePassword(AuthRequest.ChangePasswordRequest changePasswordRequest);
    User getOrCreateUser(String email, EmailType emailType);
}
