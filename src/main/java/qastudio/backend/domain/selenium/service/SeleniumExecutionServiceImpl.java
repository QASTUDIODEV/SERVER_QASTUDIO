package qastudio.backend.domain.selenium.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.selenium.dto.ActionExecutionResult;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.util.SeleniumActionExecutor;
import qastudio.backend.domain.test.dto.request.TestRequest;
import qastudio.backend.domain.test.repository.ErrorRepository;
import qastudio.backend.domain.test.service.TestCommandService;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;
import qastudio.backend.global.s3.service.S3Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeleniumExecutionServiceImpl implements SeleniumExecutionService {

    private final SeleniumWebSocketHandler webSocketHandler;
    private final TestCommandService testCommandService;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    private final ErrorRepository errorRepository;

    @Override
    public SeleniumExecutionResponse executeTest(String sessionId, Long userId, SeleniumExecutionRequest request) {
        WebDriver driver = new ChromeDriver();
        List<String> executionLogs = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        SeleniumActionExecutor.setWebSocketHandler(webSocketHandler);
        SeleniumActionExecutor.setS3Service(s3Service);
        SeleniumActionExecutor.setErrorRepository(errorRepository);

        try {
            driver.get(request.getTargetUrl());
            executionLogs.add("URL 접근: " + request.getTargetUrl());

            int totalActions = request.getActions().size();
            int executedActions = 0;
            Integer errorCode = null;
            String errorMessage = null;
            String errorImage = null;

            for (SeleniumExecutionRequest.ActionDetail action : request.getActions()) {
                executionLogs.add("➡ Step " + action.getStep() + ": " + action.getActionDescription());
                ActionExecutionResult result = SeleniumActionExecutor.performAction(driver, action, sessionId, executionLogs);

                if (!result.isSuccess()) {
                    errorCode = result.getErrorCode();
                    errorMessage = result.getErrorMessage();
                    errorImage = result.getErrorImage();
                } else {
                    executedActions++;
                }
            }

            executionLogs.add("✅ 테스트 완료");
            String scenarioRecord = convertActionsToJson(request);
            int attainment = (int) (((double) executedActions / totalActions) * 100);

            // **1️⃣ 테스트 데이터 저장**
            Long testId = testCommandService.createTest(new TestRequest(
                    "Test Run - " + request.getTargetUrl(),
                    attainment,
                    errorCode == null ? "SUCCESS" : "FAILURE",
                    (System.currentTimeMillis() - startTime) / 1000.0,
                    null, null, null,  // errorId를 아직 저장 안함
                    userId,
                    request.getProjectId(),
                    request.getPageId(),
                    scenarioRecord,
                    totalActions,
                    executedActions
            ));

            // **2️⃣ 오류 발생 시 error 테이블에 저장**
            Long errorId = null;
            if (errorCode != null) {
                qastudio.backend.domain.test.entity.Error error = errorRepository.saveError(errorCode, errorMessage, errorImage, testId);
                errorId = error.getId();
            }

            // **3️⃣ 테스트 정보 업데이트 (errorId 저장)**
            if (errorId != null) {
                testCommandService.updateTestErrorId(testId, errorId);
            }

            return new SeleniumExecutionResponse(errorId == null ? "SUCCESS" : "FAILURE", executionLogs);

        } catch (Exception e) {
            executionLogs.add("❌ 실행 중 예기치 않은 오류 발생: " + e.getMessage());
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
