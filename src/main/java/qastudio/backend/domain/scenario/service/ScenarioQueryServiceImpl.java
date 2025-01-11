package qastudio.backend.domain.scenario.service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScenarioQueryServiceImpl implements ScenarioQueryService {

    @Override
    public ScenarioDetailResponse getScenarioById(Long scenarioId) {
        return null;
    }
}
