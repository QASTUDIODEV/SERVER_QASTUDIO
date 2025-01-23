package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;

public interface CharacterQueryService {
    List<CharacterTable> getProjectCharacter(Long projectId);

    CharacterResponse.DetailCharacterList getDetailCharacterList(Long projectId);

    CharacterResponse.ScenarioList getScenarioList(Long characterId);

    CharacterResponse.ProjectPathList getProjectPaths(Long projectId);
}
