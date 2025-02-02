package qastudio.backend.domain.project.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Map;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.dto.response.CharacterResponse.Character;
import qastudio.backend.domain.project.dto.response.CharacterResponse.CharacterList;
import qastudio.backend.domain.project.dto.response.CharacterResponse.ScenarioList;
import qastudio.backend.domain.project.entity.*;
import qastudio.backend.domain.scenario.converter.ScenarioConverter;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.user.entity.User;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class CharacterConverter {
    private final ScenarioConverter scenarioConverter;

    public CharacterConverter(ScenarioConverter scenarioConverter) {
        this.scenarioConverter = scenarioConverter;
    }

    public Character toCharacterAndScenario(CharacterTable characterTable, List<Scenario> scenarios) {
        ScenarioList scenarioList = scenarioConverter.toScenarioList(scenarios);
        
        return Character.builder()
                .characterId(characterTable.getId())
                .characterName(characterTable.getCharacterName())
                .characterDescription(characterTable.getCharacterDescription())
                .author(characterTable.getUser().getNickname())
                .createdAt(characterTable.getCreatedAt())
                .updatedAt(characterTable.getUpdatedAt())
                .scenarios(scenarioList)
                .build();
    }

    public CharacterList toCharacterList(org.springframework.data.domain.Page<CharacterTable> characterTablePage, Map<Long, List<Scenario>> scenarioMap) {
        List<Character> characterList = characterTablePage.stream()
                .map(characterTable -> {
                    // 캐릭터 ID별 시나리오 리스트 가져오기
                    List<Scenario> scenarios = scenarioMap.getOrDefault(characterTable.getId(), new ArrayList<>());
                    return toCharacterAndScenario(characterTable, scenarios);
                })
                .collect(Collectors.toList());

        return CharacterList.builder()
                .characters(characterList)
                .listSize(characterList.size())
                .totalPage(characterTablePage.getTotalPages())
                .totalElements(characterTablePage.getTotalElements())
                .isFirst(characterTablePage.isFirst())
                .isLast(characterTablePage.isLast())
                .build();
    }

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
