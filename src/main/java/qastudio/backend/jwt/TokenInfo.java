package qastudio.backend.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfo {
    private String type;
    private String accessToken;
    private String refreshToken;
}
