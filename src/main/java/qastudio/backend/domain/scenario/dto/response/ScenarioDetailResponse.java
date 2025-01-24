package qastudio.backend.domain.scenario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

import java.util.List;

@Getter
@Builder
public class ScenarioDetailResponse {

    @Schema(description = "시나리오 ID", example = "1")
    private Long scenarioId;

    @Schema(description = "시나리오 이름", example = "Login Test")
    private String scenarioName;

    @Schema(description = "시나리오 설명", example = "로그인 기능 테스트 시나리오")
    private String scenarioDescription;

    @Schema(description = "액션 리스트")
    private List<ActionDetail> actions;

    @Getter
    @Builder
    public static class ActionDetail {
        @Schema(description = "액션 ID", example = "1")
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
}
