package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;

public interface ScenarioCommandService {
    ScenarioResponse createScenario(ScenarioRequest.CreateScenarioRequest request);
}
