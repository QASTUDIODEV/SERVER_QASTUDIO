package qastudio.backend.domain.scenario.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.springframework.stereotype.Service;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

@Service
@RequiredArgsConstructor
public class SeleniumService {

    private final SeleniumWebSocketHandler seleniumWebSocketHandler;

    private void performAction(WebDriver driver, String sessionId, By locator, String action, String value) {
        WebElement element = driver.findElement(locator);

        if ("send_keys".equals(action)) {
            element.sendKeys(value);
        } else if ("click".equals(action)) {
            element.click();
        }

        // 현재 HTML & CSS 전송
        sendHtmlAndCss(driver, sessionId);
    }

    private void sendHtmlAndCss(WebDriver driver, String sessionId) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String html = (String) js.executeScript("return document.documentElement.outerHTML;");
        String css = (String) js.executeScript(
                "let styles = ''; " +
                        "for (let s of document.styleSheets) { " +
                        "    if (s.cssRules) { " +
                        "        for (let r of s.cssRules) { styles += r.cssText + '\\n'; } " +
                        "    } " +
                        "} " +
                        "return styles;"
        );

        seleniumWebSocketHandler.sendHtmlAndCss(sessionId, html, css);
    }
}

