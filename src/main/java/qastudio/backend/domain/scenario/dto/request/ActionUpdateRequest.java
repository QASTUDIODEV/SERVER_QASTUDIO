package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ActionUpdateRequest {

    @Schema(description = "선택 방법", example = "css_selector")
    @NotEmpty(message = "선택 방법은 필수 값입니다.")
    private String locatorType;

    @Schema(description = "요소 값", example = "#email")
    @NotEmpty(message = "요소 값은 필수 값입니다.")
    private String locatorValue;

    @Schema(description = "행동 종류", example = "send_keys")
    @NotEmpty(message = "행동 종류는 필수 값입니다.")
    private String actionType;

    @Schema(description = "행동 값", example = "testuser@example.com")
    private String actionValue;
}
