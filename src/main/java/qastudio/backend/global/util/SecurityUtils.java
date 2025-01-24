package qastudio.backend.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return null; // 비로그인 사용자 처리
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            return Long.parseLong(((User) principal).getUsername()); // `username`을 `userId`로 사용
        }

        return null;
    }
}
