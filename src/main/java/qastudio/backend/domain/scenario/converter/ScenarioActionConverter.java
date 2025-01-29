package qastudio.backend.domain.scenario.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;
import qastudio.backend.domain.scenario.dto.response.FeatureJson;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

@Component
@RequiredArgsConstructor
public class ScenarioActionConverter {

    private final ObjectMapper objectMapper;

    public ScenarioDetailResponse.ActionDetail toActionDetail(ActionTable action, Long scenarioId, Feature feature) {
        try {
            FeatureJson featureJson = objectMapper.readValue(feature.getFeatureJson(), FeatureJson.class);

            return ScenarioDetailResponse.ActionDetail.builder()
                    .actionId(action.getId())
                    .actionDescription(action.getActionDescription())
                    .step(action.getStep())
                    .actionType(action.getActionType())
                    .locator(featureJson.getLocator())
                    .action(featureJson.getAction())
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage());
        }
    }
}
