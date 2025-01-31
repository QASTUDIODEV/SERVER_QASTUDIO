
package qastudio.backend.domain.scenario.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.repository.CharacterTableRepository.CharacterTableRepository;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.scenario.dto.request.ActionRequest;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.repository.FeatureRepository;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.scenario.service.ScenarioCommandService;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;

import qastudio.backend.domain.user.entity.User;


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

        if (scenarios.size() != scenarioIds.size()) {
            throw new EntityNotFoundException("일부 시나리오가 존재하지 않습니다.");
        }

        scenarioRepository.deleteAll(scenarios);
    }
}
