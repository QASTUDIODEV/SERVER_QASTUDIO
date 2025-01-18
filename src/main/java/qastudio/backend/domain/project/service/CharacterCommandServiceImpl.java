package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.converter.CharacterConverter;
import qastudio.backend.domain.project.dto.request.CharacterRequest.CreateCharacter;
import qastudio.backend.domain.project.dto.response.CharacterResponse.CharacterScenario;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
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
    private final CharacterConverter characterConverter;


    @Override
    public CharacterScenario createCharacter(Long projectId, CreateCharacter createCharacter) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("프로젝트가 존재하지 않습니다."));

        CharacterTable characterTable = characterConverter.toCharacter(createCharacter, project);

        CharacterTable savedCharacterTable = characterTableRepository.save(characterTable);

        // 시나리오 도메인에서 converter로 분리 필요
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
}
