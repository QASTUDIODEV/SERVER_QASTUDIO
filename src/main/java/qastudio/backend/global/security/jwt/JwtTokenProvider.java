package qastudio.backend.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final long ACCESS_TOKEN_DURATION = 1000 * 60 * 30L; // 30분
    private static final long REFRESH_TOKEN_DURATION = 1000 * 60 * 60L * 24 * 7; // 7일

    private final SecretKey secretKey;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}") String key, StringRedisTemplate redisTemplate) {
        byte[] keyBytes = key.getBytes();
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    // 토큰 생성 (공통 메서드)
    public TokenInfo generateToken(Long userId, Authentication authentication) {
        String accessToken = generateAccessToken(userId, authentication);
        String refreshToken = generateRefreshToken(userId, authentication);

        redisTemplate.opsForValue().set("refresh:" + userId, refreshToken, REFRESH_TOKEN_DURATION, TimeUnit.MILLISECONDS);

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
    private String generateRefreshToken(Long userId, Authentication authentication) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + REFRESH_TOKEN_DURATION);

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
            jwtBuilder.claim("auth", "USER");
        }

        return jwtBuilder.compact();
    }

    // 유효성 검사
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.getSubject();

            // 로그아웃된 토큰인지 확인
            if (redisTemplate.hasKey("logout:" + userId)) {
                log.warn("🚨 [JwtTokenProvider] 블랙리스트된 Token (Logged Out): {}", token);
                return false;
            }

            log.info("✅ [JwtTokenProvider] Token 검증 성공: {}", token);
            return true;

        } catch (ExpiredJwtException e) {
            log.error("🚨 [JwtTokenProvider] Token 만료: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("🚨 [JwtTokenProvider] 유효하지 않은 Token: {}", e.getMessage());
            return false;
        }
    }

    // refreshToken 검증 후 재발급
    public TokenInfo reissueToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        String userId = claims.getSubject();

        if (redisTemplate.hasKey("logout:" + userId)) {
            throw new TokenException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + userId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new TokenException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        redisTemplate.delete("refresh:" + userId);

        // 새 AccessToken 생성
        Authentication authentication = getAuthentication(refreshToken);
        return generateToken(Long.parseLong(userId), authentication);
    }

    // 로그아웃
    public void logout(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        String userId = claims.getSubject();

        // Redis에서 리프레시 토큰 삭제 (완전한 로그아웃 처리)
        redisTemplate.delete("refresh:" + userId);
        redisTemplate.opsForValue().set("logout:" + userId, "true", ACCESS_TOKEN_DURATION, TimeUnit.MILLISECONDS);
    }

    // 인증 정보 가져오기
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

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
        Claims claims = parseClaims(token);

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new TokenException(ErrorStatus.INVALID_TOKEN);
        }
    }
}