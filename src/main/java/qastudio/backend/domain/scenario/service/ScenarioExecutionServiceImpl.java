package qastudio.backend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.scenario.dto.request.ScenarioExecutionRequest;
import qastudio.backend.domain.scenario.dto.response.ExecutionResultResponse;
import qastudio.backend.global.util.HtmlCssFormatter;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class ScenarioExecutionServiceImpl implements ScenarioExecutionService {

    private final SeleniumWebSocketHandler seleniumWebSocketHandler;

    @Override
    public ExecutionResultResponse executeScenario(Long scenarioId, ScenarioExecutionRequest request) {
        WebDriver driver = new ChromeDriver();
        List<String> executionLogs = new ArrayList<>();

        try {
            driver.get(request.getTargetUrl());
            executionLogs.add("URL 접근: " + request.getTargetUrl());

            for (ScenarioExecutionRequest.Action action : request.getActions()) {
                executionLogs.add("Step " + action.getStep() + " - 액션 수행: " + action.getActionName());

                for (ScenarioExecutionRequest.Action.Element element : action.getElements()) {
                    performAction(driver, element, executionLogs);
                }

                sendHtmlAndCss(driver, request.getSessionId());
            }

            executionLogs.add("시나리오 실행 완료");
            return new ExecutionResultResponse("SUCCESS", executionLogs, getCurrentPageHtml(driver), getCurrentPageCss(driver));

        } catch (Exception e) {
            executionLogs.add("실행 중 오류 발생: " + e.getMessage());
            return new ExecutionResultResponse("FAILURE", executionLogs, null, null);
        } finally {
            driver.quit();
        }
    }

    private void performAction(WebDriver driver, ScenarioExecutionRequest.Action.Element element, List<String> logs) {
        try {
            By locator = getByLocator(element.getLocator().getStrategy(), element.getLocator().getValue());
            WebElement webElement = driver.findElement(locator);

            switch (element.getAction().getType()) {
                case "send_keys":
                    webElement.sendKeys(element.getAction().getValue());
                    logs.add("입력: " + element.getAction().getValue() + " → " + element.getName());
                    break;
                case "click":
                    webElement.click();
                    logs.add("클릭 실행: " + element.getName());
                    break;
                default:
                    logs.add("알 수 없는 액션: " + element.getAction().getType());
            }
        } catch (NoSuchElementException e) {
            logs.add("요소 찾기 실패: " + element.getName());
        }
    }

    private By getByLocator(String strategy, String value) {
        switch (strategy) {
            case "css_selector":
                return By.cssSelector(value);
            case "xpath":
                return By.xpath(value);
            case "id":
                return By.id(value);
            case "class_name":
                return By.className(value);
            case "name":
                return By.name(value);
            default:
                throw new IllegalArgumentException("지원되지 않는 locator 전략: " + strategy);
        }
    }

    private void sendHtmlAndCss(WebDriver driver, String sessionId) {
        String html = HtmlCssFormatter.formatHtml(getCurrentPageHtml(driver));
        String css = HtmlCssFormatter.formatCss(getCurrentPageCss(driver));

        seleniumWebSocketHandler.sendHtmlAndCss(sessionId, html, css);
    }

    private String getCurrentPageHtml(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return document.documentElement.outerHTML;");
    }

    private String getCurrentPageCss(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return Array.from(document.styleSheets).map(s => s.ownerNode.innerHTML).join('\\n');");
    }
}
