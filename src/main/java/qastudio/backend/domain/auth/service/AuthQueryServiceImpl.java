package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService {

    private final AccountTableRepository accountTableRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean existsEmail(String email) {
        return accountTableRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findUserIdByEmailAndEmailType(String email, EmailType emailType) {
        AccountTable account = accountTableRepository.findByEmailAndEmailType(email, emailType)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
        return account.getUser().getId();
    }
}