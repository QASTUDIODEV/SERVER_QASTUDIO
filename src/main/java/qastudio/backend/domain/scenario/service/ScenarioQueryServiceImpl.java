package qastudio.backend.domain.scenario.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;
import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse.ActionDetail;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ScenarioQueryServiceImpl implements ScenarioQueryService {

    private final ScenarioRepository scenarioRepository;
    private final ActionTableRepository actionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public ScenarioDetailResponse getScenarioById(Long scenarioId) {
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new RuntimeException("해당 시나리오를 찾을 수 없습니다."));

        List<ActionTable> actions = actionRepository.findByScenarioId(scenarioId);
        List<ActionDetail> actionDetails = actions.stream().map(action -> {
            List<ScenarioDetailResponse.Element> elements = convertJsonToElements(action.getActionType());
            return new ActionDetail(
                    action.getActionDescription(),
                    action.getStep(),
                    elements
            );
        }).toList();
        return ScenarioDetailResponse.builder()
                .id(scenario.getId())
                .scenarioName(scenario.getScenarioName())
                .scenarioDescription(scenario.getScenarioDescription())
                .actions(actionDetails)
                .build();
    }

    private List<ScenarioDetailResponse.Element> convertJsonToElements(String json) {
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, ScenarioDetailResponse.Element.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 실패: " + e.getMessage());
        }
    }
}
