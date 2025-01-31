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
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.util.SecurityUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ActionCommandServiceImpl implements ActionCommandService {

    private final ActionTableRepository actionRepository;
    private final FeatureRepository featureRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ActionTableRepository actionTableRepository;
    private final ScenarioRepository scenarioRepository;
    @Transactional
    @Override
    public ActionResponse updateAction(Long actionId, ActionUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();

        ActionTable action = actionRepository.findById(actionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 액션을 찾을 수 없습니다. actionId: " + actionId));
        updateActionTable(action, request);

        Feature existingFeature = featureRepository.findFeatureByUserOrDefault(userId, actionId)
                .orElse(null);

        Feature feature;
        if (existingFeature != null && existingFeature.getUser() != null) {
            feature = updateFeature(existingFeature, request);
        } else {
            feature = createNewFeature(userId, action, request);
        }

        return buildActionResponse(action, request);
    }

    private void updateActionTable(ActionTable action, ActionUpdateRequest request) {
        action = ActionTable.builder()
                .id(action.getId())
                .actionDescription(request.getActionDescription())
                .step(request.getStep())
                .actionType(request.getActionType())
                .scenario(action.getScenario()) // 기존 시나리오 정보 유지
                .features(action.getFeatures())
                .build();

        actionRepository.save(action);
    }

    private Feature updateFeature(Feature feature, ActionUpdateRequest request) {
        try {
            String updatedFeatureJson = objectMapper.writeValueAsString(Map.of(
                    "locator", request.getLocator(),
                    "action", request.getAction()
            ));            feature = Feature.builder()
                    .id(feature.getId())
                    .featureJson(updatedFeatureJson)
                    .user(feature.getUser()) // 기존 사용자 유지
                    .action(feature.getAction()) // 기존 액션 유지
                    .build();

            return featureRepository.save(feature);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage());
        }
    }
    private Feature createNewFeature(Long userId, ActionTable action, ActionUpdateRequest request) {
        try {
            String newFeatureJson = objectMapper.writeValueAsString(Map.of(
                    "locator", request.getLocator(),
                    "action", request.getAction()
            ));
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. userId: " + userId));

            Feature newFeature = Feature.builder()
                    .featureJson(newFeatureJson)
                    .user(user)
                    .action(action)
                    .build();

            return featureRepository.save(newFeature);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage());
        }
    }

    private ActionResponse buildActionResponse(ActionTable action, ActionUpdateRequest request) {
        return ActionResponse.builder()
                .actionId(action.getId())
                .actionDescription(action.getActionDescription())
                .step(action.getStep())
                .actionType(action.getActionType())
                .locator(request.getLocator())
                .action(request.getAction())
                .build();
    }
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
}
