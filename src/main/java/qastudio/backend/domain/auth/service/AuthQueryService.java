package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.user.entity.enums.EmailType;

public interface AuthQueryService {
    Long findUserIdByEmailAndEmailType(String email, EmailType emailType);
}

