package qastudio.backend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.converter.ActionConverter;
import qastudio.backend.domain.scenario.dto.request.ActionUpdateRequest;
import qastudio.backend.domain.scenario.dto.response.ActionResponse;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.service.ActionCommandService;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ActionCommandServiceImpl implements ActionCommandService {

    private final ActionTableRepository actionRepository;
    private final ActionConverter actionConverter;

    @Override
    public void createActionsForScenario(Long scenarioId, List<ScenarioRequest.CreateScenarioRequest.Action> actions) {
        List<ActionTable> actionEntities = actions.stream()
                .map(action -> actionConverter.toEntity(action, scenarioId))
                .toList();
        actionRepository.saveAll(actionEntities);
    }



    @Override
    public ActionResponse updateAction(Long actionId, ActionUpdateRequest request) {
        ActionTable action = actionRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action not found"));
        actionRepository.save(action);
        return null;
    }
}
