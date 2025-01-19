
package qastudio.backend.domain.scenario.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.converter.ScenarioConverter;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.scenario.service.ScenarioCommandService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Transactional
public class ScenarioCommandServiceImpl implements ScenarioCommandService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConverter scenarioConverter;

    @Override
    public ScenarioResponse createScenario(ScenarioRequest.CreateScenarioRequest request) {
        Scenario scenario = scenarioConverter.toEntity(request);
        Scenario savedScenario = scenarioRepository.save(scenario);
        return scenarioConverter.toResponse(savedScenario);
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
