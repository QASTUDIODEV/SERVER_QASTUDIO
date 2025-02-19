package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import qastudio.backend.domain.selenium.dto.ActionExecutionResult;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;
import qastudio.backend.domain.test.repository.ErrorRepository;
import qastudio.backend.global.s3.dto.AwsDTO;
import qastudio.backend.global.s3.service.S3Service;
import qastudio.backend.global.util.HtmlCssFormatter;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;



public class SeleniumActionExecutor {

    private static SeleniumWebSocketHandler webSocketHandler;
    private static S3Service s3Service;
    private static ErrorRepository errorRepository;

    private SeleniumActionExecutor() {}

    public static void setWebSocketHandler(SeleniumWebSocketHandler handler) {
        webSocketHandler = handler;
    }
    public static void setS3Service(S3Service service) {
        s3Service = service;
    }
    public static void setErrorRepository(ErrorRepository repository) {
        errorRepository = repository;
    }


    public static ActionExecutionResult performAction(WebDriver driver, SeleniumExecutionRequest.ActionDetail actionDetail, String sessionId, List<String> logs) {
        try {
            WebElement webElement = findAndHighlightElement(driver, actionDetail, sessionId, logs);
            if (webElement == null) {
                throw new NoSuchElementException("Locator not found: " + actionDetail.getLocator().getValue());
            }

            ActionType actionType = convertActionType(actionDetail);
            LocatorActionValidator.validate(LocatorType.fromString(actionDetail.getLocator().getStrategy()), actionType);

            ActionExecutor.executeAction(driver, webElement, actionType, actionDetail, logs);
            if (actionType == ActionType.CLICK) {
                new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            }
            checkForNetworkErrors(driver, logs);

            sendHtmlAndCssUpdate(driver, sessionId, logs, actionDetail.getActionId(), "SUCCESS", "AFTER_ACTION");
            return new ActionExecutionResult(1, null, null, null);

        } catch (NoSuchElementException e) {
            return handleDetailedError(driver, actionDetail, sessionId, logs, "LOCATOR_NOT_FOUND", e);
        } catch (ElementNotInteractableException e) {
            return handleDetailedError(driver, actionDetail, sessionId, logs, "LOCATOR_NOT_CLICKABLE", e);
        } catch (TimeoutException e) {
            return handleDetailedError(driver, actionDetail, sessionId, logs, "TIMEOUT_ERROR", e);
        } catch (UnhandledAlertException e) {
            return handleDetailedError(driver, actionDetail, sessionId, logs, "UNEXPECTED_ALERT", e);
        } catch (WebDriverException e) {
            if (e.getMessage().contains("disconnected") || e.getMessage().contains("Session ID is null")) {
                return handleDetailedError(driver, actionDetail, sessionId, logs, "SESSION_EXPIRED", e);
            }
            return handleDetailedError(driver, actionDetail, sessionId, logs, "UNKNOWN_ERROR", e);
        } catch (Exception e) {
            return handleDetailedError(driver, actionDetail, sessionId, logs, "UNKNOWN_ERROR", e);
        }
    }
    private static void checkForNetworkErrors(WebDriver driver, List<String> logs) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String networkErrors = (String) js.executeScript(
                    "return performance.getEntriesByType('resource')"
                            + ".filter(e => e.initiatorType === 'xmlhttprequest' || e.initiatorType === 'fetch')"
                            + ".map(e => e.name + ' - ' + (e.responseEnd - e.startTime) + 'ms').join('\\n');"
            );

            if (!networkErrors.isEmpty()) {
                logs.add("[NETWORK ERROR] 감지된 API 응답 실패: \n" + networkErrors);
            } else {
                logs.add("[NETWORK CHECK] 모든 API 요청이 정상적으로 처리됨.");
            }
        } catch (Exception e) {
            logs.add("[NETWORK CHECK ERROR] 네트워크 로그 확인 중 오류 발생: " + e.getMessage());
        }
    }
    private static ActionExecutionResult handleDetailedError(WebDriver driver, SeleniumExecutionRequest.ActionDetail actionDetail, String sessionId, List<String> logs, String errorCode, Exception e) {
        logs.add("❌ [" + errorCode + "] 오류 발생: " + actionDetail.getActionDescription() + " - " + e.getMessage());

        String serverErrorMessage = getServerErrorMessage(driver);
        if (!serverErrorMessage.isEmpty()) {
            logs.add("서버 응답 오류: " + serverErrorMessage);
            errorCode = "SERVER_ERROR"; // 서버 응답 오류가 감지되면 errorCode를 변경
        }

        String imageUrl = captureScreenshotAndUpload(driver);
        webSocketHandler.sendFailureMessage(sessionId, actionDetail.getActionId(), "FAIL", "AFTER_ACTION", e.getMessage());

        return new ActionExecutionResult(0, 500, e.getMessage(), imageUrl);
    }

    private static String getServerErrorMessage(WebDriver driver) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String response = (String) js.executeScript(
                    "return performance.getEntriesByType('resource')"
                            + ".filter(e => e.initiatorType === 'xmlhttprequest' || e.initiatorType === 'fetch')"
                            + ".map(e => e.name + ' - ' + (e.responseEnd - e.startTime) + 'ms').join('\\n');"
            );
            return response.isEmpty() ? "No network errors detected" : response;
        } catch (Exception e) {
            return "Error while fetching network logs: " + e.getMessage();
        }
    }

    private static WebElement findAndHighlightElement(WebDriver driver, SeleniumExecutionRequest.ActionDetail actionDetail, String sessionId, List<String> logs) throws InterruptedException {
        WebElement webElement = findElementSafely(driver, actionDetail);
        highlightElement(driver, webElement);
        sendHtmlAndCssUpdate(driver, sessionId, logs, actionDetail.getActionId(), "IN_PROGRESS", "BEFORE_ACTION");
        unhighlightElement(driver, webElement);
//        sleep(3000);
        new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
        return webElement;
    }

    private static ActionType convertActionType(SeleniumExecutionRequest.ActionDetail actionDetail) {
        String actionTypeString = actionDetail.getAction().getType();
        if ("navigate".equalsIgnoreCase(actionTypeString) || "click".equalsIgnoreCase(actionTypeString)) {
            actionTypeString = "click";
        } else if ("Fill_Text".equalsIgnoreCase(actionTypeString)) {
            actionTypeString = "send_keys";
        }
        return ActionType.fromString(actionTypeString);
    }



    private static void highlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].classList.add('highlighted-selenium-element')", element);
    }

    private static void unhighlightElement(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].classList.remove('highlighted-selenium-element')", element);
    }
    private static WebElement findElementSafely(WebDriver driver, SeleniumExecutionRequest.ActionDetail actionDetail) {
        try {
            LocatorType locatorType = LocatorType.fromString(actionDetail.getLocator().getStrategy());
            return new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(LocatorUtils.getByLocator(locatorType, actionDetail.getLocator().getValue())));
        } catch (TimeoutException e) {
            return null;
        }
    }

    private static String captureScreenshotAndUpload(WebDriver driver) {
        if (s3Service == null) return null;

        try {
            // 브라우저 스크린샷을 바이트 배열로 변환
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = "selenium_error_" + System.currentTimeMillis() + ".png";

            // S3에 업로드할 Presigned URL 요청
            AwsDTO.PresignedUploadRequest uploadRequest = new AwsDTO.PresignedUploadRequest();
            Field field = AwsDTO.PresignedUploadRequest.class.getDeclaredField("fileName");
            field.setAccessible(true);
            field.set(uploadRequest, fileName);

            AwsDTO.PresignedUrlUploadResponse uploadResponse = s3Service.getPresignedUrlToUpload(uploadRequest);
            if (uploadResponse == null || uploadResponse.getUrl() == null) return null;

            // Presigned URL로 S3에 업로드
            uploadToS3(uploadResponse.getUrl(), screenshotBytes);

            // S3에서 실제 저장된 정적 URL 반환
            return s3Service.generateStaticUrl(uploadResponse.getKeyName());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void uploadToS3(String presignedUrl, byte[] fileBytes) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(presignedUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "image/png");

        connection.getOutputStream().write(fileBytes);
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            throw new IOException("S3 업로드 실패 - 응답 코드: " + responseCode);
        }
    }

    public static void sendHtmlAndCssUpdate(WebDriver driver, String sessionId, List<String> logs, Long actionId, String status, String phase) {
        if (webSocketHandler != null) {
            try {
//                String formattedHtml = HtmlCssFormatter.formatHtml(driver.getPageSource());
                String formattedHtml = SeleniumHtmlCssUtil.getCurrentPageHtmlWithInputs(driver);
                String formattedCss = HtmlCssFormatter.formatCss(getCurrentPageCss(driver));

                formattedCss += "\n.highlighted-selenium-element { border: 3px solid red; }";
                logs.add("실시간 HTML & CSS 전송");
                webSocketHandler.sendHtmlAndCss(sessionId, formattedHtml, formattedCss, actionId, status, phase);
            } catch (Exception e) {
                logs.add("❌ HTML & CSS 전송 실패: " + e.getMessage());
            }
        }
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
