package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

@Getter
@NoArgsConstructor
public class ActionUpdateRequest {

    @Schema(description = "액션 설명", example = "Enter Email")
    @NotEmpty(message = "액션 설명은 필수 값입니다.")
    private String actionDescription;

    @Schema(description = "액션 단계", example = "1")
    @NotNull(message = "액션 단계는 필수 값입니다.")
    private Integer step;

    @Schema(description = "액션 유형", example = "input")
    @NotEmpty(message = "액션 유형은 필수 값입니다.")
    private String actionType;

    @Schema(description = "Locator 정보")
    @NotNull(message = "Locator 정보는 필수 값입니다.")
    private SeleniumExecutionRequest.Locator locator;

    @Schema(description = "Action 정보")
    @NotNull(message = "Action 정보는 필수 값입니다.")
    private SeleniumExecutionRequest.Action action;
}
