package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import java.util.stream.Collectors;

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

}
