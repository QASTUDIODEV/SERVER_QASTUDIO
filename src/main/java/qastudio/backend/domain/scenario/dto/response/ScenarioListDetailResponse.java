package qastudio.backend.domain.scenario.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ScenarioListDetailResponse {

    @Schema(description = "캐릭터 ID", example = "1")
    private Long characterId;

    @Schema(description = "시나리오 상세 목록")
    private List<ScenarioDetailResponse> scenarios;
}
