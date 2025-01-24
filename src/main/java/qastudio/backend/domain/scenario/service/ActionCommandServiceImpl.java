package qastudio.backend.domain.scenario.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.dto.request.ActionUpdateRequest;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ActionResponse;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.repository.FeatureRepository;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.scenario.service.ActionCommandService;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ActionCommandServiceImpl implements ActionCommandService {

    private final ActionTableRepository actionTableRepository;
    private final FeatureRepository featureRepository;
    private final ScenarioRepository scenarioRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void createActionsForScenario(Long scenarioId, List<ScenarioRequest.ActionRequest> actions) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new EntityNotFoundException("Scenario not found"));

        for (ScenarioRequest.ActionRequest actionReq : actions) {
            ActionTable action = ActionTable.builder()
                    .actionDescription(actionReq.getActionDescription())
                    .step(actionReq.getStep())
                    .actionType(actionReq.getActionType())
                    .scenario(scenario)
                    .build();
            actionTableRepository.save(action);

            String featureJson;
            try {
                featureJson = objectMapper.writeValueAsString(Map.of(
                        "locator", actionReq.getLocator(),
                        "action", actionReq.getAction()
                ));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize JSON", e);
            }

            Feature feature = Feature.builder()
                    .featureJson(featureJson)
                    .user(null)
                    .action(action)
                    .build();
            featureRepository.save(feature);
        }
    }

    @Override
    public ActionResponse updateAction(Long actionId, ActionUpdateRequest request) {
        ActionTable action = actionTableRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action not found"));
        actionTableRepository.save(action);
        return null;
    }
}
