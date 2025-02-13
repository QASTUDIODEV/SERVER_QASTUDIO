package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import qastudio.backend.global.util.HtmlCssFormatter;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.util.List;


public class SeleniumHtmlCssUtil {

    public static void sendHtmlAndCssUpdate(WebDriver driver, String sessionId, List<String> logs, Long actionId, String status, String phase, SeleniumWebSocketHandler webSocketHandler) {
        if (webSocketHandler != null) {
            try {
                String formattedHtml = HtmlCssFormatter.formatHtml(driver.getPageSource());
                String formattedCss = HtmlCssFormatter.formatCss(getCurrentPageCss(driver));

                logs.add("실시간 HTML & CSS 전송");
                webSocketHandler.sendHtmlAndCss(sessionId, formattedHtml, formattedCss, actionId, status, phase);
            } catch (Exception e) {
                logs.add("❌ HTML & CSS 전송 실패: " + e.getMessage());
            }
        }
    }
    public static String getCurrentPageHtmlWithInputs(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript(
                "document.querySelectorAll('input, textarea').forEach(el => el.setAttribute('value', el.value)); " +
                        "return document.documentElement.outerHTML;"
        );
    }
    public static String getCurrentPageCss(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // 현재 DOM에서 적용된 스타일만 가져오기
        String css = (String) js.executeScript(
                "let extractedCss = ''; " +
                        "for (let sheet of document.styleSheets) { " +
                        "    try { " +
                        "        for (let rule of sheet.cssRules) { " +
                        "            extractedCss += rule.cssText + '\\n'; " +
                        "        } " +
                        "    } catch (e) { console.log('CSS Access Denied: ' + e.message); } " +
                        "} " +
                        "return extractedCss;"
        );

        return css;
    }
}