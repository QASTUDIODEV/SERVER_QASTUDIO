package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterQueryServiceImpl implements CharacterQueryService{
    @Override
    public List<CharacterTable> getProjectCharacter(Long projectId) {
        return List.of();
    }

    @Override
    public CharacterResponse.DetailCharacterList getDetailCharacterList(Long projectId) { return null; }

    @Override
    public CharacterResponse.ScenarioList getScenarioList(Long characterId) { return null; }

    @Override
    public CharacterResponse.CharacterScenario createCharacter(CharacterRequest.CreateCharacter createCharacter) { return null; }

}
