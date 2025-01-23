package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.time.Duration;
import java.util.List;

public class SeleniumActionExecutor {

    private static SeleniumWebSocketHandler webSocketHandler;

    private SeleniumActionExecutor() {}

    public static void setWebSocketHandler(SeleniumWebSocketHandler handler) {
        webSocketHandler = handler;
    }

    public static void performAction(WebDriver driver, SeleniumExecutionRequest.Action action, String sessionId, List<String> logs) {
        try {
            SeleniumExecutionRequest.Element element = action.getElement();
            LocatorType locatorType = LocatorType.fromString(element.getLocator().getStrategy());
            WebElement webElement = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(LocatorUtils.getByLocator(locatorType, element.getLocator().getValue())));

            ActionType actionType = ActionType.fromString(element.getAction().getType());

            LocatorActionValidator.validate(locatorType, actionType);

            ActionExecutor.executeAction(webElement, actionType, element, logs);

            sendHtmlAndCssUpdate(driver, sessionId, logs);

        } catch (UnsupportedOperationException e) {
            logs.add("❌ 지원되지 않는 액션 오류: " + e.getMessage());
        } catch (Exception e) {
            logs.add("❌ 요소 찾기 실패 또는 실행 오류: " + action.getElement().getName() + " - 오류: " + e.getMessage());
        }
    }

    private static void sendHtmlAndCssUpdate(WebDriver driver, String sessionId, List<String> logs) {
        if (webSocketHandler != null) {
            try {
                String html = driver.getPageSource();
                String css = getCurrentPageCss(driver);

                logs.add("📡 실시간 HTML & CSS 전송");
                webSocketHandler.sendHtmlAndCss(sessionId, html, css);
            } catch (Exception e) {
                logs.add("❌ HTML & CSS 전송 실패: " + e.getMessage());
            }
        }
    }

    private static String getCurrentPageCss(WebDriver driver) {
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "let styles = ''; " +
                        "for (let s of document.styleSheets) { " +
                        "  try { " +
                        "    if (s.cssRules) { " +
                        "      for (let r of s.cssRules) { styles += r.cssText + '\\n'; } " +
                        "    } " +
                        "  } catch (e) {} " +
                        "} return styles;"
        );
    }
}
