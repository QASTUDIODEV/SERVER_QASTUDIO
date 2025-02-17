package qastudio.backend.domain.scenario.service;


import qastudio.backend.domain.scenario.dto.request.ActionRequest;
import qastudio.backend.domain.scenario.dto.request.ActionUpdateRequest;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ActionResponse;

import java.util.List;

public interface ActionCommandService {
    ActionResponse updateAction(Long actionId, ActionUpdateRequest request);
    void createActionsForScenario(Long scenarioId, List<ScenarioRequest.ActionRequest> actions);
    void deleteActionsForScenario(Long scenarioId);
}
