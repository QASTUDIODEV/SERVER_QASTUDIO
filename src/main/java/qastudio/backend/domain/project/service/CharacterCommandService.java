package qastudio.backend.domain.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;

public interface CharacterCommandService {
    CharacterResponse.CharacterScenario createCharacter(Long projectId, CharacterRequest.CreateCharacter createCharacter, String token) throws JsonProcessingException;

    CharacterResponse.CharacterScenario updateCharacter(Long projectId, Long characterId, Long scenarioId, CharacterRequest.UpdateCharacter updateCharacter, String token) throws JsonProcessingException;

    void deleteCharacters(List<Long> characterIds);
}
