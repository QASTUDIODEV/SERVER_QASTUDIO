package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.user.entity.enums.EmailType;

public interface AuthQueryService {
    boolean isEmailExists(String email);
    Long findUserIdByEmailAndEmailType(String email, EmailType emailType);
}

