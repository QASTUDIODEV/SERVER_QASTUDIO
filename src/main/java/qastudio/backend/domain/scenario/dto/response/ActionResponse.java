package qastudio.backend.domain.scenario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

@Getter
@Builder
public class ActionResponse {

    @Schema(description = "액션 ID", example = "10")
    private Long actionId;

    @Schema(description = "액션 설명", example = "Enter Email")
    private String actionDescription;

    @Schema(description = "액션 단계", example = "1")
    private Integer step;

    @Schema(description = "액션 유형", example = "input")
    private String actionType;

    @Schema(description = "Locator 정보")
    private SeleniumExecutionRequest.Locator locator;

    @Schema(description = "Action 정보")
    private SeleniumExecutionRequest.Action action;
}
