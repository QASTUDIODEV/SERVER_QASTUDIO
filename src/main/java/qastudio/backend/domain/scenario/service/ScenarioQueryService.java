package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

public interface ScenarioQueryService {
    SeleniumExecutionRequest getExecutionRequestByScenarioId(Long scenarioId);

}
