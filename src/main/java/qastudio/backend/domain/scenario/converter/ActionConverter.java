package qastudio.backend.domain.scenario.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.scenario.dto.Element;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Scenario;

import java.util.List;

@Component
public class ActionConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ActionTable toEntity(ScenarioRequest.CreateScenarioRequest.Action action, Long scenarioId) {
        String actionElements = convertToJson(action.getElements());

        return ActionTable.builder()
                .actionName(action.getActionName())
                .step(action.getStep())
                .scenario(Scenario.builder().id(scenarioId).build())
                .actionElements(actionElements)
                .build();
    }
    private String convertToJson(List<Element> elements) {
        try {
            return objectMapper.writeValueAsString(elements);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패: " + e.getMessage());
        }
    }
}
