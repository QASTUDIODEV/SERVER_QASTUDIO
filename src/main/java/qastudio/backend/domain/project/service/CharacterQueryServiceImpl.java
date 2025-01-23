package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.converter.CharacterConverter;
import qastudio.backend.domain.project.dto.request.CharacterRequest;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CharacterQueryServiceImpl implements CharacterQueryService{

    private final CharacterTableRepository characterRepository;
    private final CharacterConverter characterConverter;
    private final PageRepository pageRepository;

    @Override
    public List<CharacterTable> getProjectCharacter(Long projectId) {
        return List.of();
    }

    @Override
    public CharacterResponse.DetailCharacterList getDetailCharacterList(Long projectId) {
        List<CharacterTable> characterTables = characterRepository.findAllByProjectId(projectId);
        return characterConverter.toDetailCharacterList(characterTables, projectId);
    }

    @Override
    public CharacterResponse.ScenarioList getScenarioList(Long characterTableId) {
        List<Scenario> scenarios = characterRepository.findAllByCharacterTableId(characterTableId);
        return characterConverter.toScenarioList(scenarios);
    }

    @Override
    public CharacterResponse.ProjectPathList getProjectPaths(Long projectId) {
        List<Page> pages = pageRepository.findAllByProjectId(projectId);
        return CharacterConverter.toProjectPathList(pages);
    }
}
