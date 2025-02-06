package qastudio.backend.domain.selenium.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.selenium.dto.ActionExecutionResult;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.util.SeleniumActionExecutor;
import qastudio.backend.domain.test.dto.request.TestRequest;
import qastudio.backend.domain.test.entity.enums.State;
import qastudio.backend.domain.test.repository.ErrorRepository;
import qastudio.backend.domain.test.service.TestCommandService;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;
import qastudio.backend.global.s3.service.S3Service;

import java.net.MalformedURLException;
import java.net.URL;
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
        WebDriver driver = createRemoteWebDriver();
        List<String> executionLogs = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        initializeSelenium(sessionId);

        try {
            driver.get(request.getTargetUrl());
            executionLogs.add("URL 접근: " + request.getTargetUrl());

            ActionExecutionResult executionResult = executeActions(driver, request, sessionId, executionLogs);

            executionLogs.add("테스트 완료");
            int attainment = calculateAttainment(request.getActions().size(), executionResult.getExecutedActions());

//            driver.quit();

            // 테스트 데이터 저장
            Long testId = saveTest(request, userId, executionResult, startTime, attainment);
            executionLogs.add("테스트 데이터 저장");

            // 오류 발생 시 error 테이블에 저장
            Long errorId = executionResult.hasError() ? saveError(executionResult, testId) : null;
            executionLogs.add("오류 데이터 저장");

            // 테스트 정보 업데이트 (errorId 저장)
            if (errorId != null) {
                testCommandService.updateTestErrorId(testId, errorId);
            }

            return new SeleniumExecutionResponse(errorId == null ? State.SUCCESS.name() : State.FAIL.name(), executionLogs);

        } catch (Exception e) {
            executionLogs.add("❌ 실행 중 예기치 않은 오류 발생: " + e.getMessage());
            return new SeleniumExecutionResponse("FAIL", executionLogs);
        } finally {
            driver.quit();
        }
    }


    private WebDriver createRemoteWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
//        options.addArguments("--remote-allow-origins=*");
        try {
            return new RemoteWebDriver(new URL("https://dev.qa-studio.com/wd/hub"), options);
        } catch (MalformedURLException e) {
            System.out.println(e);
            throw new RuntimeException("Invalid remote WebDriver URL", e);
        }
    }

    private void initializeSelenium(String sessionId) {
        SeleniumActionExecutor.setWebSocketHandler(webSocketHandler);
        SeleniumActionExecutor.setS3Service(s3Service);
        SeleniumActionExecutor.setErrorRepository(errorRepository);
    }

    private ActionExecutionResult executeActions(WebDriver driver, SeleniumExecutionRequest request, String sessionId, List<String> executionLogs) {
        int executedActions = 0;
        Integer errorCode = null;
        String errorMessage = null;
        String errorImage = null;

        for (SeleniumExecutionRequest.ActionDetail action : request.getActions()) {
            if (webSocketHandler.shouldStopExecution(sessionId)) {
                executionLogs.add("실행이 중지되었습니다.");
                break;
            }
            executionLogs.add("➡ Step " + action.getStep() + ": " + action.getActionDescription());
            ActionExecutionResult result = SeleniumActionExecutor.performAction(driver, action, sessionId, executionLogs);

            if (result.hasError()) {
                errorCode = result.getErrorCode();
                errorMessage = result.getErrorMessage();
                errorImage = result.getErrorImage();
                break;
            } else {
                executedActions++;
            }
        }

        return new ActionExecutionResult(executedActions, errorCode, errorMessage, errorImage);
    }
    private Long saveTest(SeleniumExecutionRequest request, Long userId, ActionExecutionResult executionResult, long startTime, int attainment) {
        return testCommandService.createTest(new TestRequest(
                "Test Run - " + request.getTargetUrl(),
                attainment,
                executionResult.hasError() ? State.FAIL : State.SUCCESS,
                (System.currentTimeMillis() - startTime) / 1000.0,
                userId,
                request.getProjectId(),
                request.getPageId(),
                convertActionsToJson(request),
                request.getActions().size(),
                executionResult.getExecutedActions()
        ));
    }
    private Long saveError(ActionExecutionResult executionResult, Long testId) {
        return errorRepository.saveErrorAndGetId(
                executionResult.getErrorCode(),
                executionResult.getErrorMessage(),
                executionResult.getErrorImage(),
                testId
        );
    }
    private int calculateAttainment(int totalActions, int executedActions) {
        return (int) (((double) executedActions / totalActions) * 100);
    }




    private String convertActionsToJson(SeleniumExecutionRequest request) {
        try {
            return objectMapper.writeValueAsString(request.getActions());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류: " + e.getMessage(), e);
        }
    }
}
