package qastudio.backend.domain.project.converter;

import java.util.Optional;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import java.util.stream.Collectors;
import qastudio.backend.domain.project.entity.UserProject;

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
}
