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
import qastudio.backend.domain.selenium.dto.request.CustomExecutionRequest;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.util.CustomActionExecutor;
import qastudio.backend.domain.selenium.util.SeleniumActionExecutor;
import qastudio.backend.domain.selenium.util.SeleniumHtmlCssUtil;
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

import static qastudio.backend.global.util.MemoryUtils.logMemoryUsage;

@Service
@RequiredArgsConstructor
public class SeleniumExecutionServiceImpl implements SeleniumExecutionService {

    private final SeleniumWebSocketHandler webSocketHandler;
    private final TestCommandService testCommandService;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    private final ErrorRepository errorRepository;

    public SeleniumExecutionResponse fetchPageSource(Long userId, String targetUrl) {
        WebDriver driver = createRemoteWebDriver();

//        WebDriver driver = new ChromeDriver();
        List<String> executionLogs = new ArrayList<>();

        try {
            logMemoryUsage("실행 전 JVM 메모리 상태");
            driver.get(targetUrl);
            String html = driver.getPageSource();
            String css = SeleniumActionExecutor.getCurrentPageCss(driver);
            executionLogs.add("HTML 및 CSS 코드 수집 완료");
            return new SeleniumExecutionResponse("SUCCESS", executionLogs, html, css);
        } catch (Exception e) {
            executionLogs.add("❌ 실행 중 예기치 않은 오류 발생: " + e.getMessage());
            return new SeleniumExecutionResponse("FAIL", executionLogs, null, null);
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            System.gc(); // JVM 가비지 컬렉션 강제 실행
            logMemoryUsage("WebDriver 종료 후 JVM 메모리 상태");
        }
    }


    // 시나리오 실행 기본 로직
    public SeleniumExecutionResponse executeRecordActions(CustomExecutionRequest request) {
        WebDriver driver = createRemoteWebDriver();

//        WebDriver driver = new ChromeDriver(); // 로컬 테스트 용도
        try {
            List<String> executionLogs = new ArrayList<>();
            logMemoryUsage("실행 전 JVM 메모리 상태");
            driver.get(request.getUrl());
            ActionExecutionResult executionResult = executeRecordActions(driver, request, executionLogs);

            String currentHtml = driver.getPageSource();
            String currentCss = SeleniumHtmlCssUtil.getCurrentPageCss(driver);

            return new SeleniumExecutionResponse(
                    executionResult.hasError() ? State.FAIL.name() : State.SUCCESS.name(),
                    executionLogs,
                    currentHtml,
                    currentCss
            );
        } catch (Exception e) {
            return new SeleniumExecutionResponse("FAIL", List.of("❌ 실행 중 예기치 않은 오류 발생: " + e.getMessage()));
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            System.gc(); // JVM 가비지 컬렉션 강제 실행
            logMemoryUsage("WebDriver 종료 후 JVM 메모리 상태");
        }
    }

    private ActionExecutionResult executeRecordActions(WebDriver driver, CustomExecutionRequest request, List<String> executionLogs) {
        int executedActions = 0;

        for (CustomExecutionRequest.ActionDetail action : request.getActions()) {
            executionLogs.add("➡ Step " + action.getStep() + ": " + action.getActionDescription());
            ActionExecutionResult result = CustomActionExecutor.performRecordAction(driver, action, executionLogs);
        }

        return new ActionExecutionResult(executedActions, null, null, null);
    }






    @Override
    public SeleniumExecutionResponse executeTest(String sessionId, Long userId, SeleniumExecutionRequest request) {
        WebDriver driver = createRemoteWebDriver();

//        WebDriver driver = new ChromeDriver(); // 로컬 테스트 용도
        List<String> executionLogs = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        initializeSelenium(sessionId);

        try {
            logMemoryUsage("🚀 실행 전 JVM 메모리 상태");
            driver.get(request.getTargetUrl());
            executionLogs.add("URL 접근: " + request.getTargetUrl());

            ActionExecutionResult executionResult = executeActions(driver, request, sessionId, executionLogs);

            executionLogs.add("테스트 완료");
            int attainment = calculateAttainment(request.getActions().size(), executionResult.getExecutedActions());

//            driver.quit();

            Long testId = saveTest(request, userId, executionResult, startTime, attainment);
            executionLogs.add("테스트 데이터 저장");

            Long errorId = executionResult.hasError() ? saveError(executionResult, testId) : null;
            executionLogs.add("오류 데이터 저장");

            if (errorId != null) {
                testCommandService.updateTestErrorId(testId, errorId);
            }
            return new SeleniumExecutionResponse(errorId == null ? State.SUCCESS.name() : State.FAIL.name(), executionLogs, null, null, testId);
        } catch (Exception e) {
            executionLogs.add("❌ 실행 중 예기치 않은 오류 발생: " + e.getMessage());
            return new SeleniumExecutionResponse("FAIL", executionLogs);
        } finally {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
            System.gc(); // JVM 가비지 컬렉션 강제 실행
            logMemoryUsage("WebDriver 종료 후 JVM 메모리 상태");
            webSocketHandler.closeSession(sessionId);
        }
    }


    private WebDriver createRemoteWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-sync");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--blink-settings=imagesEnabled=false");
        options.addArguments("--disk-cache-size=104857600");
        options.addArguments("--ignore-ssl-errors=yes");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-component-extensions-with-background-pages");
        options.addArguments("--disable-translate");
        options.addArguments("--disable-features=TranslateUI");
        options.addArguments(("--metrics-recording-only"));
        try {
            String remoteUrl = "http://selenium-chrome:4444/wd/hub";
            return new RemoteWebDriver(new URL(remoteUrl), options);
        } catch (MalformedURLException e) {
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
