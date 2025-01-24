package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;
import qastudio.backend.global.util.HtmlCssFormatter;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.time.Duration;
import java.util.List;

public class SeleniumActionExecutor {

    private static SeleniumWebSocketHandler webSocketHandler;

    private SeleniumActionExecutor() {}

    public static void setWebSocketHandler(SeleniumWebSocketHandler handler) {
        webSocketHandler = handler;
    }

    public static void performAction(WebDriver driver, SeleniumExecutionRequest.ActionDetail actionDetail, String sessionId, List<String> logs) {
        try {
            LocatorType locatorType = LocatorType.fromString(actionDetail.getLocator().getStrategy());

            // 웹 요소 찾기
            WebElement webElement = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(LocatorUtils.getByLocator(locatorType, actionDetail.getLocator().getValue())));

            // 액션 유효성 검사
            ActionType actionType = ActionType.fromString(actionDetail.getAction().getType());
            LocatorActionValidator.validate(locatorType, actionType);

            ActionExecutor.executeAction(webElement, actionType, actionDetail, logs); // 셀레니움 액션 실행

            sendHtmlAndCssUpdate(driver, sessionId, logs);

        } catch (UnsupportedOperationException e) {
            logs.add("❌ 지원되지 않는 액션 오류: " + e.getMessage());
        } catch (Exception e) {
            logs.add("❌ 요소 찾기 실패 또는 실행 오류: " + actionDetail.getActionDescription() + " - 오류: " + e.getMessage());
        }
    }

    private static void sendHtmlAndCssUpdate(WebDriver driver, String sessionId, List<String> logs) {
        if (webSocketHandler != null) {
            try {
                String formattedHtml = HtmlCssFormatter.formatHtml(driver.getPageSource());
                String formattedCss = HtmlCssFormatter.formatCss(getCurrentPageCss(driver));


                logs.add("📡 실시간 HTML & CSS 전송");
                webSocketHandler.sendHtmlAndCss(sessionId, formattedHtml, formattedCss);
            } catch (Exception e) {
                logs.add("❌ HTML & CSS 전송 실패: " + e.getMessage());
            }
        }
    }

    private static String getCurrentPageCss(WebDriver driver) {
        return (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "let css = ''; " +
                        "document.querySelectorAll('style').forEach(style => { " +
                        "    css += style.innerHTML + '\\n'; " +
                        "}); " +
                        "document.querySelectorAll('*').forEach(element => { " +
                        "    let computedStyle = window.getComputedStyle(element); " +
                        "    for (let i = 0; i < computedStyle.length; i++) { " +
                        "        css += element.tagName + '{' + computedStyle[i] + ':' + computedStyle.getPropertyValue(computedStyle[i]) + ';}\\n'; " +
                        "    } " +
                        "}); " +
                        "return css;"
        );
    }


}
