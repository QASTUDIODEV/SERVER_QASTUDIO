package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.request.ScenarioExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;

public interface ScenarioExecutionService {
    SeleniumExecutionResponse executeScenario(ScenarioExecutionRequest request);
}
