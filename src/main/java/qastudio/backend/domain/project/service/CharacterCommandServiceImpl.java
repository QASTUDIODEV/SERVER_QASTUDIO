package qastudio.backend.domain.project.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.converter.CharacterConverter;
import qastudio.backend.domain.project.dto.request.CharacterRequest.CreateCharacter;
import qastudio.backend.domain.project.dto.request.CharacterRequest.UpdateCharacter;
import qastudio.backend.domain.project.dto.response.CharacterResponse.CharacterScenario;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.PageRole;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.PageRole.PageRoleRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CharacterCommandServiceImpl implements CharacterCommandService {

    private final CharacterTableRepository characterTableRepository;
    private final ProjectRepository projectRepository;
    private final ScenarioRepository scenarioRepository;
    private final PageRoleRepository pageRoleRepository;
    private final PageRepository pageRepository;
    private final CharacterConverter characterConverter;


    @Override
    public CharacterScenario createCharacter(Long projectId, CreateCharacter createCharacter) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트가 존재하지 않습니다."));

        CharacterTable characterTable = characterConverter.toCharacter(createCharacter, project);

        CharacterTable savedCharacterTable = characterTableRepository.save(characterTable);

        // 시나리오 도메인에서 converter로 분리 필요?
        for (String accessPage : createCharacter.getAccessPage()) {
            Scenario scenario = Scenario.builder()
                    .scenarioName(accessPage)
                    .scenarioDescription("시나리오 설명")
                    .characterTable(savedCharacterTable)
                    .build();
            scenarioRepository.save(scenario);
        }

        return characterConverter.toCharacterScenario(savedCharacterTable, createCharacter.getAccessPage());
    }

    @Override
    public CharacterScenario updateCharacter(Long characterId, UpdateCharacter updateCharacter) {
        // accessPage 수정 안 되는 문제 해결 필요

        CharacterTable characterTable = characterTableRepository.findById(characterId)
                .orElseThrow(() -> new EntityNotFoundException("역할이 존재하지 않습니다."));

        characterTable.updateCharacter(
                updateCharacter.getCharacterName(),
                updateCharacter.getCharacterDescription()
        );

        List<String> accessPagePaths = updateCharacter.getAccessPage();
        List<Page> requestedPages = pageRepository.findAllByPathIn(accessPagePaths);

        List<PageRole> existingPageRoles = pageRoleRepository.findAllByCharacterId(characterId);
        List<Page> existingPages = existingPageRoles.stream()
                .map(PageRole::getPage)
                .toList();

        List<Page> pagesToAdd = requestedPages.stream()
                .filter(page -> !existingPages.contains(page))
                .toList();

        for (Page page : pagesToAdd) {
            PageRole newPageRole = PageRole.builder()
                    .characterTable(characterTable)
                    .page(page)
                    .build();
            pageRoleRepository.save(newPageRole);
        }

        List<PageRole> rolesToRemove = existingPageRoles.stream()
                .filter(pageRole -> !requestedPages.contains(pageRole.getPage()))
                .collect(Collectors.toList());

        if (!rolesToRemove.isEmpty()) {
            pageRoleRepository.deleteAll(rolesToRemove);
        }

        characterTableRepository.save(characterTable);

        List<PageRole> updatedPageRoles = pageRoleRepository.findAllByCharacterId(characterId);
        return characterConverter.toCharacterScenarioResponse(characterTable, updatedPageRoles);
    }
}
