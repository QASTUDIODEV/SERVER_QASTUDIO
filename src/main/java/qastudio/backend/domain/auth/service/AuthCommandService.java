package qastudio.backend.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.domain.auth.dto.response.AuthResponse;
import qastudio.backend.global.security.jwt.TokenInfo;

public interface AuthCommandService {
    void userSignUp(AuthRequest.LocalRequest request, HttpServletResponse response);
    AuthResponse.LoginResponse localLogin(AuthRequest.LocalRequest loginRequest, HttpServletResponse response);
    TokenInfo authenticateAndGenerateToken(String email, String password);
    void changePassword(AuthRequest.ChangePasswordRequest changePasswordRequest);
    void reissueToken(HttpServletRequest request,  HttpServletResponse response);
}
