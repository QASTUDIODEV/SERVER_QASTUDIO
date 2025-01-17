package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.auth.dto.request.EmailRequest;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailQueryServiceImpl implements EmailQueryService {

    private final AccountTableRepository accountTableRepository;

    @Override
    public void checkEmailDuplication(EmailRequest emailRequest) {

        accountTableRepository.findByEmailAndEmailType(emailRequest.getEmail(), EmailType.LOCAL)
                .ifPresent(account -> {
                    throw new BadRequestException(ErrorStatus.ALREADY_EXIST_EMAIL);
                });
    }
}
