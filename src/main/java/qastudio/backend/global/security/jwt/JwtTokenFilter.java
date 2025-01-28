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

        try {
            processTokenAuthentication(request);
        } catch (TokenException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid Token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // 인증 필터 제외 경로
    private boolean isExcluded(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String skipAuth = request.getParameter("skipAuth"); // 쿼리 파라미터 확인

        if (uri.startsWith("/oauth2/authorization") && "true".equals(skipAuth)) {
            return true;
        }

        // 기본 인증 제외 경로 처리
        return uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/api/v0/auth/sign-up") || // 회원가입 제외
                uri.startsWith("/api/v0/auth/login") ||   // 로그인 제외
                uri.startsWith("/api/v0/auth/update/password") ||   // 비밀번호 변경 제외
                uri.startsWith("/api/v0/auth/check") ||   // 토큰 확인 제외
                uri.startsWith("/api/v0/auth/sign-up/email") || // 이메일 인증 제외
                uri.startsWith("/css") ||
                uri.startsWith("/js") ||
                uri.startsWith("/images") ||
                uri.equals("/default-ui.css") ||
                uri.equals("/favicon.ico") ||
                uri.equals("/health");
    }

    // JWT 토큰 인증 처리
    private void processTokenAuthentication(HttpServletRequest request) {
        String token = getToken(request);

        if (jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return;
        }

        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        throw new TokenException(ErrorStatus.INVALID_TOKEN);
    }

//    // Authorization 헤더에서 토큰 추출
//    private String getToken(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }

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