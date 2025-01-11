package qastudio.backend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.dto.request.EnvironmentRequest;
import qastudio.backend.domain.scenario.dto.response.ExecutionResultResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScenarioExecutionServiceImpl implements ScenarioExecutionService {

    @Override
    public ExecutionResultResponse executeScenario(Long scenarioId, EnvironmentRequest request) {
        return null;
    }

    @Override
    public void stopScenario(Long executionId) {
        System.out.println("Scenario execution stopped: " + executionId);
    }
}
