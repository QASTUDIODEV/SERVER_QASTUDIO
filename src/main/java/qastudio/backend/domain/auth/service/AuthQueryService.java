package qastudio.backend.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;

public interface AuthQueryService {
    User findUserIdByEmailAndEmailType(String email, EmailType emailType);
    String getCookieValue(HttpServletRequest request, String name);
}

