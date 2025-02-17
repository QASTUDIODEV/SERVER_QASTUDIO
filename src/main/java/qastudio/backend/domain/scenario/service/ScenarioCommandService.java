package qastudio.backend.domain.scenario.service;

import java.util.List;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;

public interface ScenarioCommandService {
    ScenarioResponse createScenario(ScenarioRequest.CreateScenarioRequest request, Long userId);

    void deleteScenarios(List<Long> scenarioIds);
    ScenarioResponse updateScenario(Long scenarioId, ScenarioRequest.UpdateScenarioRequest request, Long userId);
}
