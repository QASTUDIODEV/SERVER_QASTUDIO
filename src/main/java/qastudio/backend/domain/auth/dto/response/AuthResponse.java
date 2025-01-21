package qastudio.backend.domain.auth.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthResponse {
    @NotBlank
    private String accessToken;

    @NotBlank
    private String refreshToken;
}
