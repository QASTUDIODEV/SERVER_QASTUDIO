package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.request.ScenarioExecutionRequest;
import qastudio.backend.domain.scenario.dto.response.ExecutionResultResponse;

public interface ScenarioExecutionService {
    ExecutionResultResponse executeScenario(Long scenarioId, ScenarioExecutionRequest request);
}