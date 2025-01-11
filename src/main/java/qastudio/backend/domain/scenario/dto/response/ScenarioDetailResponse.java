package qastudio.backend.domain.scenario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ScenarioDetailResponse {

    @Schema(description = "시나리오 ID", example = "1")
    private Long id;

    @Schema(description = "시나리오 이름", example = "Login Test Scenario")
    private String scenarioName;

    @Schema(description = "시나리오 설명", example = "이 시나리오는 로그인 동작을 테스트합니다.")
    private String scenarioDescription;

    @Schema(description = "액션 목록")
    private List<ActionDetail> actions;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ActionDetail {

        @Schema(description = "액션 이름", example = "Click Login Button")
        private String actionName;

        @Schema(description = "액션 종류", example = "click")
        private String actionType;

        @Schema(description = "액션 단계", example = "1")
        private Integer step;
    }
}
