package qastudio.backend.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.exception.custom.TokenException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class InviteTokenProvider {

    private static final long REFRESH_TOKEN_DURATION = 1000 * 60 * 60L * 24 * 7; // 7일

    private final SecretKey secretKey;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public InviteTokenProvider(@Value("${jwt.secret}") String key, StringRedisTemplate redisTemplate) {
        byte[] keyBytes = key.getBytes();
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.redisTemplate = redisTemplate;
    }

    public String generateToken(Long projectId, Long userId, String email) {
        Claims claims = Jwts.claims().setSubject("ProjectUserInfo");
        claims.put("projectId", projectId);
        claims.put("userId", userId);
        claims.put("email", email);

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expiryDate = new Date(nowMillis + REFRESH_TOKEN_DURATION);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // Claims 파싱
    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorStatus.EXPIRED_INVITATION_TOKEN);
        } catch (JwtException e) {
            throw new AuthException(ErrorStatus.INVALID_TOKEN);
        }
    }
}
