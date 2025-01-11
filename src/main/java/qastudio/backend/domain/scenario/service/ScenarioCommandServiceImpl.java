
package qastudio.backend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;
import qastudio.backend.domain.scenario.service.ScenarioCommandService;

@Service
@RequiredArgsConstructor
public class ScenarioCommandServiceImpl implements ScenarioCommandService {

    @Override
    public ScenarioResponse createScenario(ScenarioRequest.CreateScenarioRequest request) {
        return null;
    }
}
