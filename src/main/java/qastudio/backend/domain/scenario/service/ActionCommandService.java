package qastudio.backend.domain.scenario.service;


import qastudio.backend.domain.scenario.dto.request.ActionUpdateRequest;
import qastudio.backend.domain.scenario.dto.response.ActionResponse;

public interface ActionCommandService {
    ActionResponse updateAction(Long actionId, ActionUpdateRequest request);
}
