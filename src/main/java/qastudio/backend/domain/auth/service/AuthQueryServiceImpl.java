package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService {

    private final AccountTableRepository accountTableRepository;

    @Override
    public User findUserIdByEmailAndEmailType(String email, EmailType emailType) {
        AccountTable account = accountTableRepository.findByEmailAndEmailType(email, emailType)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
        return account.getUser();
    }
}
