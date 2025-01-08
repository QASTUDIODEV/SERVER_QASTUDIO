package qastudio.backend.domain.auth.dto.response;

import jakarta.validation.constraints.NotBlank;

public record LoginResponse(@NotBlank String accessToken, @NotBlank String refreshToken) {
}
