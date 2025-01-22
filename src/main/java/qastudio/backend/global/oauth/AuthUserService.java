package qastudio.backend.global.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
public class AuthUserService {

    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(ErrorStatus.TOKEN_ERROR);
        }

        // SecurityContext에서 로그인된 사용자 ID 가져오기
        String userIdString = authentication.getName();
        Long userId;

        try {
            userId = Long.parseLong(userIdString);
        } catch (NumberFormatException e) {
            throw new AuthException(ErrorStatus.INVALID_USER_ID_FORMAT);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorStatus.USER_NOT_FOUND));
    }
}