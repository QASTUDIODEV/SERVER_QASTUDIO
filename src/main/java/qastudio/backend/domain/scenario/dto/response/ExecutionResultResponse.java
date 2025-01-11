package qastudio.backend.domain.scenario.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ExecutionResultResponse {

    @Schema(description = "시나리오 ID", example = "1")
    private Long scenarioId;

    @Schema(description = "시나리오 실행 상태", example = "COMPLETED")
    private String status;

    @Schema(description = "액션 실행 결과 목록")
    private List<ActionResult> actions;

    @Getter
    @Builder
    public static class ActionResult {
        @Schema(description = "액션 이름", example = "Click")
        private String actionName;

        @Schema(description = "타겟 요소", example = "#loginButton")
        private String target;

        @Schema(description = "성공 여부", example = "true")
        private boolean success;

        @Schema(description = "에러 메시지", example = "null")
        private String errorMessage;
    }
}
