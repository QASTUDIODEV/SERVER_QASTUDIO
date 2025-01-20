package qastudio.backend.domain.auth.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import qastudio.backend.domain.auth.converter.EmailConverter;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.auth.dto.response.EmailResponse;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailQueryServiceImpl implements EmailQueryService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final AccountTableRepository accountTableRepository;
    private final AuthQueryService authQueryService;
    private final EmailConverter emailConverter;
    private String randomCode;

    // 랜덤 코드 8자 생성
    @Override
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
    @Override
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {
        createCode();
        String setFrom = "qastudio7@gmail.com"; // email-config에 설정한 자신의 이메일 주소
        String toEmail = email; // 받는 사람
        String title = "QASTUDIO 인증 번호입니다."; // 제목

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, toEmail); // 보낼 이메일 설정
        message.setSubject(title);
        message.setFrom(setFrom);
        message.setText(setContext(randomCode), "utf-8", "html");

        return message;
    }

    // 회원가입 시, 인증번호 이메일 전송
    @Override
    public EmailResponse sendSignEmail(EmailRequest emailRequest) throws BadRequestException {
        // 이메일 중복 검사
        checkEmailDuplication(emailRequest);

        try {
            MimeMessage emailForm = createEmailForm(emailRequest.getEmail());
            emailSender.send(emailForm);

            return emailConverter.toEmailResponse(randomCode);
        } catch (UnsupportedEncodingException | MessagingException e){
            throw new BadRequestException(ErrorStatus.EMAIL_VERIFICATION_SEND_FAILED);
        }
    }

    // 비밀번호 변경 시, 인증번호 이메일 전송
    @Override
    public EmailResponse sendPasswordEmail(EmailRequest emailRequest) throws BadRequestException {
        authQueryService.findUserIdByEmailAndEmailType(emailRequest.getEmail(), EmailType.LOCAL);

        try {
            MimeMessage emailForm = createEmailForm(emailRequest.getEmail());
            emailSender.send(emailForm);

            return emailConverter.toEmailResponse(randomCode);
        } catch (UnsupportedEncodingException | MessagingException e){
            throw new BadRequestException(ErrorStatus.EMAIL_VERIFICATION_SEND_FAILED);
        }
    }

    // context 설정
    @Override
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context);
    }

    @Override
    public void checkEmailDuplication(EmailRequest emailRequest) {

        accountTableRepository.findByEmailAndEmailType(emailRequest.getEmail(), EmailType.LOCAL)
                .ifPresent(account -> {
                    throw new BadRequestException(ErrorStatus.ALREADY_EXIST_EMAIL);
                });
    }
}
