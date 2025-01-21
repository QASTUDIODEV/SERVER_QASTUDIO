package qastudio.backend.domain.project.service;

import java.util.List;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;

public interface CharacterCommandService {
    CharacterResponse.CharacterScenario createCharacter(Long projectId, CharacterRequest.CreateCharacter createCharacter);

    CharacterResponse.CharacterScenario updateCharacter(Long characterId, CharacterRequest.UpdateCharacter updateCharacter);

    void deleteCharacters(List<Long> characterIds);

    CharacterResponse.ProjectPath getProjectPath(Long projectId);
}
