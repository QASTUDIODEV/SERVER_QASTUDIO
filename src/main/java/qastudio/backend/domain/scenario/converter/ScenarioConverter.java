package qastudio.backend.domain.scenario.converter;

import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;
import qastudio.backend.domain.scenario.entity.Scenario;

@Component
public class ScenarioConverter {

    public Scenario toEntity(ScenarioRequest.CreateScenarioRequest request) {
        return Scenario.builder()
                .scenarioName(request.getScenarioName())
                .scenarioDescription(request.getScenarioDescription())
                .characterTable(CharacterTable.builder().id(request.getCharacterId()).build())
                .build();
    }

    public ScenarioResponse toResponse(Scenario scenario) {
        return ScenarioResponse.builder()
                .id(scenario.getId())
                .scenarioName(scenario.getScenarioName())
                .scenarioDescription(scenario.getScenarioDescription())
                .actionCount(0)
                .build();
    }
}
