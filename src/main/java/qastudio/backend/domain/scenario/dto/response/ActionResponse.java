package qastudio.backend.domain.scenario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ActionResponse {

    @Schema(description = "액션 ID", example = "1")
    private Long id;

    @Schema(description = "선택 방법", example = "css_selector")
    private String locatorType;

    @Schema(description = "요소 값", example = "#email")
    private String locatorValue;

    @Schema(description = "행동 종류", example = "send_keys")
    private String actionType;

    @Schema(description = "행동 값", example = "testuser@example.com")
    private String actionValue;
}
