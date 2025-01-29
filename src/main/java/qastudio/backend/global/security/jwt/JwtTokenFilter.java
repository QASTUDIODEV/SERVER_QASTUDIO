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
import org.springframework.web.filter.OncePerRequestFilter;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
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

        String token = null;
        try {
            token = getToken(request);

            if (token == null) {
                throw new TokenException(ErrorStatus.NULL_TOKEN);
            }

            if (!jwtTokenProvider.validateToken(token)) {
                throw new TokenException(ErrorStatus.INVALID_TOKEN);
            }

            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (TokenException e) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        } catch (Exception e) {
            throw new BadRequestException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }

        filterChain.doFilter(request, response);
    }

    // 인증 제외할 경로
    private boolean isExcluded(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/api/v0/auth") ||
                uri.startsWith("/css") ||
                uri.startsWith("/js") ||
                uri.startsWith("/images") ||
                uri.equals("/default-ui.css") ||
                uri.equals("/favicon.ico") ||
                uri.equals("/health");
    }

    // 쿠키에서 accessToken 추출
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