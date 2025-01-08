package qastudio.backend.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.user.entity.AccountTable;
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
        AccountTable account = accountTableRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}