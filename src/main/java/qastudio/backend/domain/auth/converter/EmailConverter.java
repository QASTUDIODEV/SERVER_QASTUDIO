package qastudio.backend.domain.auth.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.auth.dto.response.EmailResponse;

@Component
@RequiredArgsConstructor
public class EmailConverter {

    public EmailResponse toEmailResponse(String authCode) {
        return EmailResponse.builder()
                .authCode(authCode)
                .build();
    }
}
