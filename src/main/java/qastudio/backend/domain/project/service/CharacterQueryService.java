package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.response.CharacterResponse;


public interface CharacterQueryService {
    CharacterResponse.DetailCharacterList getDetailCharacterList(Long projectId);

    CharacterResponse.ProjectPathList getProjectPaths(Long projectId);
    CharacterResponse.ScenarioList getScenarioList(Long characterId);
}
