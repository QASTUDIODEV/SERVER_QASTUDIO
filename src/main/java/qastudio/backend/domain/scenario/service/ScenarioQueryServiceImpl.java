package qastudio.backend.domain.scenario.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.scenario.converter.ScenarioActionConverter;
import qastudio.backend.domain.scenario.converter.ScenarioConverter;
import qastudio.backend.domain.scenario.dto.FeatureData;
import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.repository.FeatureRepository;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.global.util.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScenarioQueryServiceImpl implements ScenarioQueryService {

    private final ScenarioRepository scenarioRepository;
    private final ActionTableRepository actionRepository;
    private final FeatureRepository featureRepository;
    private final ObjectMapper objectMapper;
    private final ScenarioActionConverter scenarioActionConverter;
    private final ScenarioConverter scenarioConverter;

    @Transactional
    @Override
    public ScenarioDetailResponse getScenarioDetail(Long scenarioId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시나리오를 찾을 수 없습니다. scenarioId: " + scenarioId));

        List<ActionTable> actions = actionRepository.findByScenarioId(scenarioId);

        List<ScenarioDetailResponse.ActionDetail> actionDetails = actions.stream()
                .map(action -> {
                    Feature feature = featureRepository.findByUserAndActionOrDefault(userId, action)
                            .orElseThrow(() -> new IllegalArgumentException("Feature를 찾을 수 없습니다. actionId: " + action.getId()));

                    return scenarioActionConverter.toActionDetail(action, scenario.getId(), feature);
                })
                .collect(Collectors.toList());

        return ScenarioDetailResponse.builder()
                .scenarioId(scenario.getId())
                .scenarioName(scenario.getScenarioName())
                .scenarioDescription(scenario.getScenarioDescription())
                .actions(actionDetails)
                .build();
    }
    @Transactional
    @Override
    public SeleniumExecutionRequest getExecutionRequestByScenarioId(Long scenarioId, String baseUrl) {
        Long userId = SecurityUtils.getCurrentUserId();

        Scenario scenario = findScenarioById(scenarioId);

        String fullTargetUrl = buildTargetUrl(baseUrl, scenario.getPage().getPath());

        List<ActionTable> actions = getActionsForScenario(scenarioId);

        List<SeleniumExecutionRequest.ActionDetail> seleniumActions = buildActionDetails(actions, userId);

        return new SeleniumExecutionRequest(
                fullTargetUrl,
                scenario.getCharacterTable().getId(),
                scenario.getCharacterTable().getProject().getId(),
                scenario.getPage().getId(),
                seleniumActions
        );
    }

    private Scenario findScenarioById(Long scenarioId) {
        return scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시나리오를 찾을 수 없습니다. scenarioId: " + scenarioId));
    }

    private String buildTargetUrl(String baseUrl, String pagePath) {
        if (!baseUrl.endsWith("/") && !pagePath.startsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + pagePath;
    }


    private List<ActionTable> getActionsForScenario(Long scenarioId) {
        return actionRepository.findByScenarioId(scenarioId);
    }

    private List<SeleniumExecutionRequest.ActionDetail> buildActionDetails(List<ActionTable> actions, Long userId) {
        return actions.stream()
                .map(action -> {
                    Feature feature = featureRepository.findFeatureByUserOrDefault(userId, action.getId())
                            .orElseThrow(() -> new IllegalArgumentException("해당 액션의 Feature를 찾을 수 없습니다. actionId: " + action.getId()));

                    FeatureData featureData = parseFeatureJson(feature.getFeatureJson());

                    return SeleniumExecutionRequest.ActionDetail.builder()
                            .actionDescription(action.getActionDescription())
                            .step(action.getStep())
                            .actionType(action.getActionType())
                            .locator(featureData.getLocator())
                            .action(featureData.getAction())
                            .build();
                }).collect(Collectors.toList());
    }

    private FeatureData parseFeatureJson(String featureJson) {
        try {
            return objectMapper.readValue(featureJson, FeatureData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterResponse.ScenarioList getScenarioList(Long characterTableId) {
        List<Scenario> scenarios = scenarioRepository.findAllByCharacterId(characterTableId);
        return scenarioConverter.toScenarioList(scenarios);
    }

}
