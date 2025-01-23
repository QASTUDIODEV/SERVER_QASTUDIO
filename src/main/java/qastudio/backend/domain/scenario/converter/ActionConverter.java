package qastudio.backend.domain.scenario.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.global.util.JsonUtil;

import java.util.List;
@Component
public class ActionConverter {

    public ActionTable toEntity(ScenarioRequest.CreateScenarioRequest.Action action, Long scenarioId) {
        return ActionTable.builder()
//                .actionName(action.getActionName())
                .step(action.getStep())
                .scenario(Scenario.builder().id(scenarioId).build())
//                .actionElements(JsonUtil.toJson(action.getElements()))
                .build();
    }

}