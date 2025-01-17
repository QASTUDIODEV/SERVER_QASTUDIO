package qastudio.backend.domain.project.converter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import java.util.stream.Collectors;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.scenario.entity.Scenario;

@Component
public class CharacterConverter {

    public static CharacterResponse.ProjectCharacterList toProjectCharacterList(List<CharacterTable> characters) {

        List<CharacterResponse.ProjectCharacter> projectCharacters = characters.stream()
                .map(character -> CharacterResponse.ProjectCharacter.builder()
                        .characterId(character.getId())
                        .characterName(character.getCharacterName())
                        .characterDescription(character.getCharacterDescription())
                        .accessRightCnt(0) // 수정 필요
                        .roleScenarioCnt(0) // 수정 필요
                        .accessRightList(List.of()) // 수정 필요
                        .scenarioList(List.of()) // 수정 필요
                        .build())
                .collect(Collectors.toList());

        return CharacterResponse.ProjectCharacterList.builder()
                .projectCharacters(projectCharacters)
                .build();
    }

    public CharacterResponse.DetailCharacter toDetailCharacter(CharacterTable characterTable, Long projectId) {
        String author = getAuthorNickname(characterTable, projectId);

        return CharacterResponse.DetailCharacter.builder()
                .characterId(characterTable.getId())
                .author(author)
                .createdAt(characterTable.getCreatedAt())
                .updatedAt(characterTable.getUpdatedAt())
                .build();
    }

    private static String getAuthorNickname(CharacterTable characterTable, Long projectId) {
        Optional<UserProject> userProject = characterTable.getProject().getUserProjects().stream()
                .filter(up -> up.getProject().getId().equals(projectId))
                .findFirst();

        return userProject.map(up -> up.getUser().getNickname()).orElse("Unknown");
    }

    public CharacterResponse.DetailCharacterList toDetailCharacterList(List<CharacterTable> characterTables, Long projectId) {
        List<CharacterResponse.DetailCharacter> detailCharacters = characterTables.stream()
                .map(characterTable -> toDetailCharacter(characterTable, projectId))
                .collect(Collectors.toList());
        return CharacterResponse.DetailCharacterList.builder()
                .detailCharacters(detailCharacters)
                .build();
    }

    public CharacterResponse.Scenario toScenario(Scenario scenario) {
        Optional<UserProject> userProject = scenario.getCharacterTable().getProject().getUserProjects().stream()
                .filter(up -> up.getProject().getId().equals(scenario.getCharacterTable().getProject().getId()))
                .findFirst();

        String author = userProject.map(up -> up.getUser().getNickname()).orElse("Unknown");

        return CharacterResponse.Scenario.builder()
                .scenarioId(scenario.getId())
                .scenarioName(scenario.getScenarioName())
                .author(author)
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
