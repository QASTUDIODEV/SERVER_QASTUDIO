package qastudio.backend.domain.selenium.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.scenario.repository.ScenarioRepository;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.util.SeleniumActionExecutor;
import qastudio.backend.domain.test.dto.request.TestRequest;
import qastudio.backend.domain.test.service.TestCommandService;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SeleniumExecutionServiceImpl implements SeleniumExecutionService {

    private final SeleniumWebSocketHandler webSocketHandler;
    private final TestCommandService testCommandService;
    private final ObjectMapper objectMapper;

    @Override
    public SeleniumExecutionResponse executeTest(String sessionId, SeleniumExecutionRequest request) {
        WebDriver driver = new ChromeDriver();
        List<String> executionLogs = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        SeleniumActionExecutor.setWebSocketHandler(webSocketHandler);

        try {
            driver.get(request.getTargetUrl());
            executionLogs.add("URL 접근: " + request.getTargetUrl());

            int totalActions = request.getActions().size();
            int executedActions = 0;

            for (SeleniumExecutionRequest.Action action : request.getActions()) {
                executionLogs.add("➡ Step " + action.getStep() + ": " + action.getActionName());
                SeleniumActionExecutor.performAction(driver, action, sessionId, executionLogs);
                executedActions++;
            }

            executionLogs.add("✅ 테스트 완료");
            String scenarioRecord = convertActionsToJson(request);

            // ✅ JPA `save()`를 활용한 테스트 결과 저장
            testCommandService.createTest(new TestRequest(
                    "Test Run - " + request.getTargetUrl(),
                    (executedActions * 100) / totalActions,
                    "SUCCESS",
                    (System.currentTimeMillis() - startTime) / 1000.0,
                    null, null, null,
                    request.getUserId(),
                    request.getProjectId(),
                    request.getPageId(),
                    scenarioRecord,
                    totalActions,
                    executedActions
            ));

            return new SeleniumExecutionResponse("SUCCESS", executionLogs);

        } catch (Exception e) {
            executionLogs.add("❌ 실행 중 오류 발생: " + e.getMessage());

            String scenarioRecord = convertActionsToJson(request);

            testCommandService.createTest(new TestRequest(
                    "Test Run - " + request.getTargetUrl(),
                    0,
                    "FAIL",
                    (System.currentTimeMillis() - startTime) / 1000.0,
                    500, e.getMessage(), null,
                    request.getUserId(),
                    request.getProjectId(),
                    request.getPageId(),
                    scenarioRecord, // ✅ JSON 변환 확인
                    request.getActions().size(),
                    0
            ));

            return new SeleniumExecutionResponse("FAILURE", executionLogs);
        } finally {
            driver.quit();
        }
    }

    private String convertActionsToJson(SeleniumExecutionRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getActions());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage(), e);
        }
    }
}
