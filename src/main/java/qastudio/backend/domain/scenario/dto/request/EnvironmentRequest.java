package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

@Getter
@NoArgsConstructor
public class EnvironmentRequest {

    @Schema(description = "HTTP Headers", example = "{ \"Authorization\": \"Bearer token\" }")
    private Map<String, String> headers;

    @Schema(description = "Content Type", example = "application/json")
    @NotEmpty(message = "Content Type은 필수 값입니다.")
    private String contentType;
}
