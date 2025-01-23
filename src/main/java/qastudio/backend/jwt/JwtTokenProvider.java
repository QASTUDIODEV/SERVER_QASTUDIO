package qastudio.backend.jwt;

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
    public TokenInfo generateToken(Long userId, Authentication authentication, boolean isSocial) {
        String accessToken = generateAccessToken(userId, authentication, isSocial);
        String refreshToken = generateRefreshToken();

        return new TokenInfo("Bearer", accessToken, refreshToken);
    }

    // Access Token 생성
    private String generateAccessToken (Long userId, Authentication authentication, boolean isSocial) {
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
            jwtBuilder.claim("auth", authorities); // 권한 정보 추가
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
            log.error("Token is empty or null");
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token is expired", e);
        } catch (JwtException e) {
            log.error("Token is invalid", e);
        }
        return false;
    }

    // 인증 정보 가져오기
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new AuthException(ErrorStatus.MISSING_AUTHORITY);
        }

        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);
        String userId = claims.getSubject();

        UserDetails principal = User.builder()
                .username(userId)
                .password("")
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
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
            return e.getClaims();
        }
    }

    // 권한 가져오기
    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Collections.singletonList(
                new SimpleGrantedAuthority(claims.get("auth").toString())
        );
    }
}