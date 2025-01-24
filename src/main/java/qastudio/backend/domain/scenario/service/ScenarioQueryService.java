package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

public interface ScenarioQueryService {
    ScenarioDetailResponse getScenarioDetail(Long scenarioId);
    SeleniumExecutionRequest getExecutionRequestByScenarioId(Long scenarioId, String baseUrl);

}
