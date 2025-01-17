package qastudio.backend.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.EmailResponse;

import java.io.UnsupportedEncodingException;

public interface EmailQueryService {
    void createCode();
    MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException;
    EmailResponse sendEmail(EmailRequest emailRequest);
    String setContext(String code);
    void checkEmailDuplication(EmailRequest emailRequest);
}
