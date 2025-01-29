package qastudio.backend.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import qastudio.backend.global.apiPayload.code.exception.custom.TokenException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isExcluded(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = null; // 토큰 변수 추가
        try {
            token = getToken(request); // 쿠키에서 토큰 추출
            if (token == null) {
                throw new TokenException(ErrorStatus.NULL_TOKEN);
            }

            log.info("🔍 Validating token: {}", token);

            if (jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("🔍 SecurityContextHolder contains: {}", SecurityContextHolder.getContext().getAuthentication());
                log.info("✅ User authenticated: {}", authentication.getName());
            } else {
                throw new TokenException(ErrorStatus.INVALID_TOKEN);
            }
        } catch (TokenException e) {
            log.error("⛔ Invalid Token", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid Token");
            return;
        }

        // 추가 로그: 인증 정보 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            log.info("🔍 Authentication success. Principal: {}", auth.getPrincipal());
        } else {
            log.warn("⚠ Authentication failed or not present.");
        }

        filterChain.doFilter(request, response);
    }

    // 인증 필터 제외 경로
    private boolean isExcluded(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 기본 인증 제외 경로 처리
        return uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/api/v0/auth") || // 인증 경로 추가
                uri.startsWith("/css") ||
                uri.startsWith("/js") ||
                uri.startsWith("/images") ||
                uri.equals("/default-ui.css") ||
                uri.equals("/favicon.ico") ||
                uri.equals("/health");
    }

    // 쿠키에서 토큰 추출
    private String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}