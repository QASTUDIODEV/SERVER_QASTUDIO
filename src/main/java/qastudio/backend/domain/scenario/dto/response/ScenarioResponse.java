package qastudio.backend.domain.scenario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioResponse {

    @Schema(description = "시나리오 ID", example = "1")
    private Long id;

    @Schema(description = "시나리오 이름", example = "Login Test Scenario")
    private String scenarioName;

    @Schema(description = "시나리오 설명", example = "이 시나리오는 로그인 동작을 테스트합니다.")
    private String scenarioDescription;

    @Schema(description = "액션 개수", example = "3")
    private int actionCount;
}
