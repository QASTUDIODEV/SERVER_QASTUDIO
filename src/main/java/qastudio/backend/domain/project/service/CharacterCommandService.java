package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;

public interface CharacterCommandService {
    CharacterResponse.CharacterScenario createCharacter(Long projectId, CharacterRequest.CreateCharacter createCharacter);
}
