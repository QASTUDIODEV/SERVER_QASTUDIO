package qastudio.backend.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.exception.custom.TokenException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_DURATION = 1000 * 60 * 30L; // 30분
    private static final long REFRESH_TOKEN_DURATION = 1000 * 60 * 60L * 24 * 7; // 7일

    private final SecretKey secretKey;

    public JwtTokenProvider(@Value("${jwt.secret}") String key) {
        byte[] keyBytes = key.getBytes();
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성 (공통 메서드)
    public TokenInfo generateToken(Long userId, Authentication authentication) {
        String accessToken = generateAccessToken(userId, authentication);
        String refreshToken = generateRefreshToken();

        return new TokenInfo("Bearer", accessToken, refreshToken);
    }

    // Access Token 생성
    private String generateAccessToken(Long userId, Authentication authentication) {
        if (userId == null) {
            throw new AuthException(ErrorStatus.INVALID_TOKEN);
        }

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ACCESS_TOKEN_DURATION);

        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(secretKey, SignatureAlgorithm.HS256);

        if (authentication != null) {
            String authorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));
            jwtBuilder.claim("auth", authorities);
        } else {
            jwtBuilder.claim("auth", "ROLE_USER");
        }

        return jwtBuilder.compact();
    }

    // Refresh Token 생성
    private String generateRefreshToken() {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + REFRESH_TOKEN_DURATION);

        return Jwts.builder()
                .setExpiration(expiredDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 유효성 검사
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token is expired");
        } catch (JwtException e) {
            log.error("Token validation FAILED");
        }

        return false;
    }

    // 인증 정보 가져오기
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims == null) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }

        if (claims.get("auth") == null) {
            throw new TokenException(ErrorStatus.MISSING_AUTHORITY);
        }

        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        UserDetails userDetails = new User(subject, "", authorities);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );
    }

    // Claims 파싱
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorStatus.EXPIRED_TOKEN);
        } catch (JwtException e) {
            throw new AuthException(ErrorStatus.INVALID_TOKEN);
        }
    }

    // 권한 가져오기
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(
                new SimpleGrantedAuthority(claims.get("auth").toString())
        );
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();

            if (subject == null || subject.isBlank()) {
                return null;
            }

            try {
                return Long.parseLong(subject);
            } catch (NumberFormatException e) {
                return null;
            }

        } catch (JwtException e) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }
    }
}