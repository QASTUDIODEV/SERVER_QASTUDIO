package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTableRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountTableRepository accountTableRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return loadUserByEmailAndType(email, EmailType.LOCAL); // 기본적으로 LOCAL 타입 사용
    }

    public UserDetails loadUserByEmailAndType(String email, EmailType emailType) {
        AccountTable account = accountTableRepository.findByEmailAndEmailType(email, emailType)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}