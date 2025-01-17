package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.AuthRequest;
import qastudio.backend.jwt.TokenInfo;

public interface AuthCommandService {

    void userSignUp(AuthRequest.localLoginReuqest request);
    TokenInfo localLogin(AuthRequest.localLoginReuqest loginRequest);
}