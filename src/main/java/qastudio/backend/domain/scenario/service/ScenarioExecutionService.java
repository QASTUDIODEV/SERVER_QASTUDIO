package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.request.EnvironmentRequest;
import qastudio.backend.domain.scenario.dto.response.ExecutionResultResponse;

public interface ScenarioExecutionService {
    ExecutionResultResponse executeScenario(Long scenarioId, EnvironmentRequest request);
    void stopScenario(Long executionId);
}
