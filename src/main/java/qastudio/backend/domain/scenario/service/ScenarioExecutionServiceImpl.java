package qastudio.backend.domain.scenario.service;

import qastudio.backend.domain.scenario.dto.request.ScenarioExecutionRequest;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScenarioExecutionServiceImpl implements ScenarioExecutionService {

    private final SeleniumService seleniumService;

    @Override
    public SeleniumExecutionResponse executeScenario(ScenarioExecutionRequest request) {
        return null;
    }
}
