package qastudio.backend.domain.scenario.converter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.scenario.entity.Scenario;

@Component
public class ScenarioConverter {

    public CharacterResponse.Scenario toScenario(Scenario scenario) {
        return CharacterResponse.Scenario.builder()
                .scenarioId(scenario.getId())
                .scenarioName(scenario.getScenarioName())
                .author(scenario.getUser().getNickname())
                .createdAt(scenario.getCreatedAt())
                .updatedAt(scenario.getUpdatedAt())
                .build();
    }

    public CharacterResponse.ScenarioList toScenarioList(List<Scenario> scenarios) {
        List<CharacterResponse.Scenario> scenarioResponses = scenarios.stream()
                .map(this::toScenario)
                .collect(Collectors.toList());

        return CharacterResponse.ScenarioList.builder()
                .scenarioList(scenarioResponses)
                .build();
    }
}
