package qastudio.backend.domain.scenario.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;

import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;


@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioCommandServiceImpl implements ScenarioCommandService {

    private final ScenarioRepository scenarioRepository;
    private final CharacterTableRepository characterTableRepository;
    private final PageRepository pageRepository;
    private final UserRepository userRepository;

    @Override
    public ScenarioResponse createScenario(ScenarioRequest.CreateScenarioRequest request, Long userId) {
        // Character 및 Page 엔티티 조회
        CharacterTable character = characterTableRepository.findById(request.getCharacterId())
                .orElseThrow(() -> new EntityNotFoundException("Character not found"));

        Page page = pageRepository.findById(request.getPageId())
                .orElseThrow(() -> new EntityNotFoundException("Page not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        // Scenario 저장
        Scenario scenario = Scenario.builder()
                .scenarioName(request.getScenarioName())
                .scenarioDescription(request.getScenarioDescription())
                .characterTable(character)
                .page(page)
                .user(user)
                .build();
        scenarioRepository.save(scenario);

        return new ScenarioResponse(scenario.getId(), scenario.getScenarioName(), scenario.getScenarioDescription());
    }

    @Override
    public void deleteScenarios(List<Long> scenarioIds) {
        List<Scenario> scenarios = scenarioRepository.findAllById(scenarioIds);
        if (scenarioIds == null || scenarioIds.isEmpty()) {
            throw new BadRequestException(ErrorStatus.INVALID_SCENARIO_IDS);
        }

        if (scenarios == null) {
            scenarios = Collections.emptyList();
        }

        if (scenarios.size() != scenarioIds.size()) {
            throw new BadRequestException(ErrorStatus.SCENARIOS_NOT_FOUND);
        }

        scenarioRepository.deleteAll(scenarios);
    }
}
