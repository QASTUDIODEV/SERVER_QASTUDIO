package qastudio.backend.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.EmailResponse;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;
    private final AccountTableRepository accountTableRepository;
    private final SpringTemplateEngine templateEngine;
    private String randomCode;

    // 랜덤 코드 8자 생성
    public void createCode() {
        Random random = new Random();
        StringBuffer key = new StringBuffer();

        for(int i = 0; i < 8; i++) { // 8자리
            int index = random.nextInt(3);

            switch (index) {
                case 0 : // 소문자
                    key.append((char) ((int)random.nextInt(26) + 97));
                    break;
                case 1: // 대문자
                    key.append((char) ((int)random.nextInt(26) + 65));
                    break;
                case 2: // 0-9 숫자
                    key.append(random.nextInt(9));
                    break;
            }
        }
        randomCode = key.toString();
    }

    // 이메일 양식 작성
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {
        createCode();
        String setFrom = "qastudio7@gmail.com"; // email-config에 설정한 자신의 이메일 주소
        String toEmail = email; // 받는 사람
        String title = "QASTUDIO 회원가입 인증 번호입니다."; // 제목

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, toEmail); // 보낼 이메일 설정
        message.setSubject(title);
        message.setFrom(setFrom);
        message.setText(setContext(randomCode), "utf-8", "html");

        return message;
    }

    // 이메일 전송
    public EmailResponse sendEmail(EmailRequest emailRequest) throws BadRequestException {
        // 이메일 중복 검사
        accountTableRepository.findByEmailAndEmailType(emailRequest.getEmail(), EmailType.LOCAL)
                .ifPresent(account -> {
                    throw new BadRequestException(ErrorStatus.ALREADY_EXIST_EMAIL);
                });

        try {
            MimeMessage emailForm = createEmailForm(emailRequest.getEmail());
            emailSender.send(emailForm);
            return EmailResponse.builder()
                    .authCode(randomCode)
                    .build();
        } catch (UnsupportedEncodingException | MessagingException e){
            throw new BadRequestException(ErrorStatus.EMAIL_VERIFICATION_SEND_FAILED);
        }
    }

    // context 설정
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context);
    }
}
