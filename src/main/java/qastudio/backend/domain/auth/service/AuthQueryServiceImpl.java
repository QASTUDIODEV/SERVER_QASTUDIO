package qastudio.backend.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.user.entity.AccountTable;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.entity.enums.EmailType;
import qastudio.backend.domain.user.repository.AccountTable.AccountTableRepository;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthQueryServiceImpl implements AuthQueryService {

    private final UserRepository userRepository;
    private final AccountTableRepository accountTableRepository;

    @Override
    public User findUserIdByEmailAndEmailType(String email, EmailType emailType) {
        AccountTable account = accountTableRepository.findByEmailAndEmailType(email, emailType)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));
        return account.getUser();
    }

    @Override
    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public Optional<User> getAuthenticatedUserIfPresent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        try {
            Long userId = Long.parseLong(authentication.getName());
            return userRepository.findById(userId);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}