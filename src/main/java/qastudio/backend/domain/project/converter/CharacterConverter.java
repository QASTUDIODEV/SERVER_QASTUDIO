package qastudio.backend.domain.project.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.*;

import java.util.List;
import java.util.stream.Collectors;

import qastudio.backend.domain.scenario.entity.ActionTable;
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

    public CharacterTable toCharacter(CharacterRequest.CreateCharacter createCharacter, Project project) {
        return CharacterTable.builder()
                .characterName(createCharacter.getCharacterName())
                .characterDescription(createCharacter.getCharacterDescription())
                .project(project)
                .build();
    }

    public CharacterResponse.CharacterScenario toCharacterScenario(CharacterTable characterTable, List<String> accessPages, Scenario scenario, List<ActionTable> actionTables) {
        String scenarioDescriptionWithSteps = toScenarioDescription(actionTables);

        return CharacterResponse.CharacterScenario.builder()
                .characterId(characterTable.getId())
                .characterName(characterTable.getCharacterName())
                .characterDescription(characterTable.getCharacterDescription())
                .accessPage(accessPages)
                .scenarioId(scenario.getId())
                .scenarioDescription(scenarioDescriptionWithSteps)
                .build();
    }

    public String toScenarioDescription(List<ActionTable> actionTables) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<ObjectNode> actionsJsonList = new ArrayList<>();
        for (ActionTable action : actionTables) {
            ObjectNode actionJson = objectMapper.createObjectNode();
            actionJson.put("step", action.getStep());
            actionJson.put("actionDescription", action.getActionDescription());
            actionsJsonList.add(actionJson);
        }

        try {
            return objectMapper.writeValueAsString(actionsJsonList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while converting ActionTable to JSON.", e);
        }
    }

    public CharacterResponse.CharacterScenario toCharacterScenarioResponse(CharacterTable character, List<PageRole> pageRoles) {
        List<String> accessPages = pageRoles.stream()
                .map(pageRole -> pageRole.getPage().getPath())
                .collect(Collectors.toList());

        return CharacterResponse.CharacterScenario.builder()
                .characterId(character.getId())
                .characterName(character.getCharacterName())
                .characterDescription(character.getCharacterDescription())
                .accessPage(accessPages)
                .build();
    }

    public static CharacterResponse.ProjectPathList toProjectPathList(List<Page> pages) {
        List<CharacterResponse.PageInfo> paths = pages.stream()
                .map(page -> {
                    return CharacterResponse.PageInfo.builder()
                            .pageId(page.getId())
                            .path(page.getPath())
                            .build();
                })
                .toList();

        return CharacterResponse.ProjectPathList.builder()
                .projectPaths(paths)
                .build();
    }
}
