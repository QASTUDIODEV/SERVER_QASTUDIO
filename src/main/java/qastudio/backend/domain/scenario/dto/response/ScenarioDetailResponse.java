package qastudio.backend.domain.scenario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ScenarioDetailResponse {
    private Long id;
    private String scenarioName;
    private String scenarioDescription;
    private List<ActionResponse> actions;
}
