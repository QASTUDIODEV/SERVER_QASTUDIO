package qastudio.backend.domain.project.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.*;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.user.entity.User;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CharacterConverter {

    public CharacterResponse.DetailCharacter toDetailCharacter(CharacterTable characterTable) {
        // 시나리오 수와 페이지 수 계산
        int scenarioCount = characterTable.getScenarios().size();
        int pageCount = characterTable.getPageRoles().size();

        // 접근 가능한 페이지 리스트
        List<String> accessPageList = characterTable.getPageRoles().stream()
                .map(pageRole -> pageRole.getPage().getPath())
                .collect(Collectors.toList());

        // 시나리오 리스트
        List<String> scenarioList = characterTable.getScenarios().stream()
                .map(Scenario::getScenarioName)
                .collect(Collectors.toList());

        return CharacterResponse.DetailCharacter.builder()
                .characterId(characterTable.getId())
                .characterName(characterTable.getCharacterName())
                .characterDescription(characterTable.getCharacterDescription())
                .author(characterTable.getUser().getNickname())
                .pageCnt(pageCount)
                .scenarioCnt(scenarioCount)
                .accessPageList(accessPageList)
                .scenarioList(scenarioList)
                .createdAt(characterTable.getCreatedAt())
                .updatedAt(characterTable.getUpdatedAt())
                .build();
    }


    public CharacterResponse.DetailCharacterList toDetailCharacterList(List<CharacterTable> characterTables) {
        List<CharacterResponse.DetailCharacter> detailCharacters = characterTables.stream()
                .map(this::toDetailCharacter)
                .collect(Collectors.toList());
        return CharacterResponse.DetailCharacterList.builder()
                .detailCharacters(detailCharacters)
                .build();
    }

    public CharacterTable toCharacter(CharacterRequest.CreateCharacter createCharacter, User user, Project project) {
        return CharacterTable.builder()
                .characterName(createCharacter.getCharacterName())
                .characterDescription(createCharacter.getCharacterDescription())
                .project(project)
                .user(user)
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
