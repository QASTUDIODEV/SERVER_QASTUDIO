package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;

public interface ScenarioQueryService {
    ScenarioDetailResponse getScenarioById(Long scenarioId);
}
