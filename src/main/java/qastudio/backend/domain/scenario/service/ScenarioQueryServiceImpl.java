package qastudio.backend.domain.scenario.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ActionTableRepository;
import qastudio.backend.domain.scenario.repository.FeatureRepository;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest.Action;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest.Element;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest.Locator;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest.ActionDetail;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScenarioQueryServiceImpl implements ScenarioQueryService {

    private final ScenarioRepository scenarioRepository;
    private final ActionTableRepository actionRepository;
    private final FeatureRepository featureRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public SeleniumExecutionRequest getExecutionRequestByScenarioId(Long scenarioId) {
        // ✅ Scenario 조회
        Scenario scenario = scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("해당 시나리오를 찾을 수 없습니다. scenarioId: " + scenarioId));

        // ✅ Action 조회
        List<ActionTable> actions = actionRepository.findByScenarioId(scenarioId);

        // ✅ ActionTable + Feature -> SeleniumExecutionRequest.Action 변환
        List<Action> seleniumActions = actions.stream().map(action -> {
            // ✅ Feature 테이블에서 action과 연결된 feature 조회
            Feature feature = featureRepository.findByAction(action)
                    .orElseThrow(() -> new IllegalArgumentException("해당 액션의 Feature를 찾을 수 없습니다. actionId: " + action.getId()));

            try {
                // ✅ featureJson을 `Locator` 및 `ActionDetail` 객체로 변환
                Locator locator = objectMapper.readValue(feature.getFeatureJson(), Locator.class);
                ActionDetail actionDetail = objectMapper.readValue(feature.getFeatureJson(), ActionDetail.class);

                // ✅ ActionTable + Feature 조합하여 `Element` 객체 생성
                Element element = new Element(
                        action.getActionDescription(), // ✅ ActionTable에서 name 사용
                        action.getActionType(),       // ✅ ActionTable에서 type 사용
                        locator,
                        actionDetail
                );

                return new Action(
                        action.getActionDescription(), // ✅ ActionTable에서 actionName 사용
                        action.getStep(),              // ✅ ActionTable에서 step 사용
                        element
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 변환 오류: " + e.getMessage());
            }
        }).collect(Collectors.toList());

        // ✅ SeleniumExecutionRequest 생성
        return new SeleniumExecutionRequest(
                "http://localhost:3000/", // ✅ 기본 테스트 URL (이후 DB 필드에서 가져오도록 수정 가능)
                scenario.getCharacterTable().getId(), // ✅ User ID
                scenario.getCharacterTable().getId(), // ✅ Project ID (실제 로직에서는 다른 필드 참조)
                scenario.getCharacterTable().getId(), // ✅ Page ID (실제 로직에서는 다른 필드 참조)
                seleniumActions
        );
    }
}