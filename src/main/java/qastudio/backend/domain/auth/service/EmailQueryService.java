package qastudio.backend.domain.auth.service;

import qastudio.backend.domain.auth.dto.request.EmailRequest;

public interface EmailQueryService {
    void checkEmailDuplication(EmailRequest emailRequest);
}
