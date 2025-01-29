package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BaseUrlRequest {

    @Schema(description = "테스트할 사이트의 기본 URL", example = "http://localhost:3000")
    private String baseUrl;
}
