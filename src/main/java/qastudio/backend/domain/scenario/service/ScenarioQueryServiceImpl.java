package qastudio.backend.domain.scenario.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.dto.FeatureData;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.repository.FeatureRepository;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScenarioQueryServiceImpl implements ScenarioQueryService {

    private final ScenarioRepository scenarioRepository;
    private final ActionTableRepository actionRepository;
    private final FeatureRepository featureRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public SeleniumExecutionRequest getExecutionRequestByScenarioId(Long scenarioId, String baseUrl) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시나리오를 찾을 수 없습니다. scenarioId: " + scenarioId));

        String pagePath = scenario.getPage().getPath();
        if (!baseUrl.endsWith("/") && !pagePath.startsWith("/")) {
            baseUrl += "/";
        }
        String fullTargetUrl = baseUrl + pagePath;

        List<ActionTable> actions = actionRepository.findByScenarioId(scenarioId);

        List<SeleniumExecutionRequest.ActionDetail> seleniumActions = actions.stream().map(action -> {
            Feature feature = featureRepository.findByAction(action)
                    .orElseThrow(() -> new IllegalArgumentException("해당 액션의 Feature를 찾을 수 없습니다. actionId: " + action.getId()));

            try {
                FeatureData featureData = objectMapper.readValue(feature.getFeatureJson(), FeatureData.class);

                return SeleniumExecutionRequest.ActionDetail.builder()
                        .actionDescription(action.getActionDescription())
                        .step(action.getStep())
                        .actionType(action.getActionType())
                        .locator(featureData.getLocator())
                        .action(featureData.getAction())
                        .build();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 변환 오류: " + e.getMessage());
            }
        }).collect(Collectors.toList());

        return new SeleniumExecutionRequest(
                fullTargetUrl,
                scenario.getCharacterTable().getId(),
                scenario.getCharacterTable().getProject().getId(),
                scenario.getPage().getId(),
                seleniumActions
        );
    }
}
