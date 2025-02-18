package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import qastudio.backend.global.util.HtmlCssFormatter;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.util.List;


public class SeleniumHtmlCssUtil {

    public static String getCurrentPageHtmlWithInputs(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript(
                "document.querySelectorAll('input, textarea').forEach(el => el.setAttribute('value', el.value)); " +
                        "let elements = document.querySelectorAll('*'); " +
                        "let visibleHtml = ''; " +
                        "for (let el of elements) { " +
                        "    let style = window.getComputedStyle(el);" +
                        "    if (style.display !== 'none' && style.visibility !== 'hidden' && style.opacity !== '0') { " +
                        "        visibleHtml += el.outerHTML + '\\n'; " +
                        "    } " +
                        "} " +
                        "return '<html>' + document.documentElement.innerHTML.replace(document.body.innerHTML, visibleHtml) + '</html>';"
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