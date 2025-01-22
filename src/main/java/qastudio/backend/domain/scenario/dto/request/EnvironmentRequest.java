package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class EnvironmentRequest {

    @Schema(description = "WebSocket 세션 ID", example = "user123")
    @NotEmpty(message = "세션 ID는 필수입니다.")
    private String sessionId;

    @Schema(description = "테스트 대상 URL", example = "https://www.wikipedia.org")
    @NotEmpty(message = "URL은 필수입니다.")
    private String targetUrl;
}
