package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;
import qastudio.backend.global.s3.dto.AwsDTO;
import qastudio.backend.global.s3.service.S3Service;
import qastudio.backend.global.util.HtmlCssFormatter;
import qastudio.backend.global.websocket.handler.SeleniumWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

public class SeleniumActionExecutor {

    private static SeleniumWebSocketHandler webSocketHandler;
    private static S3Service s3Service;

    private SeleniumActionExecutor() {}

    public static void setWebSocketHandler(SeleniumWebSocketHandler handler) {
        webSocketHandler = handler;
    }


    public static void setS3Service(S3Service service) {
        s3Service = service;
    }

    public static int performAction(WebDriver driver, SeleniumExecutionRequest.ActionDetail actionDetail, String sessionId, List<String> logs) {
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
            Thread.sleep(1500); // TODO: WebDriverWait로 대체

            return 1;
        } catch (Exception e) {
            logs.add("❌ 요소 찾기 실패 또는 실행 오류: " + actionDetail.getActionDescription() + " - 오류: " + e.getMessage());

            // 브라우저 화면 캡처 및 S3 업로드
            String imageUrl = captureScreenshotAndUpload(driver);
            if (imageUrl != null) {
                logs.add("📸 에러 발생 시 화면 캡처 저장: " + imageUrl);
            }
            return 0;
        }
    }

    private static String captureScreenshotAndUpload(WebDriver driver) {
        if (s3Service == null) {
            return null;
        }

        try {
            //  현재 화면을 캡처하여 메모리에 저장 (바이트 배열로 변환)
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String fileName = "selenium_error_" + System.currentTimeMillis() + ".png";

            // S3에 업로드할 Presigned URL 요청
            AwsDTO.PresignedUploadRequest uploadRequest = new AwsDTO.PresignedUploadRequest();
            Field field = AwsDTO.PresignedUploadRequest.class.getDeclaredField("fileName");
            field.setAccessible(true);
            field.set(uploadRequest, fileName);

            AwsDTO.PresignedUrlUploadResponse uploadResponse = s3Service.getPresignedUrlToUpload(uploadRequest);

            if (uploadResponse == null || uploadResponse.getUrl() == null) {
                return null;
            }

            // Presigned URL로 메모리에서 바로 S3 업로드
            uploadFileToS3(uploadResponse.getUrl(), screenshotBytes);

            // S3에 실제 저장된 정적 URL 반환
            return s3Service.generateStaticUrl(uploadResponse.getKeyName());

        } catch (Exception e) {
            return null;
        }
    }


    private static void uploadFileToS3(String presignedUrl, byte[] fileBytes) throws IOException {
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


    private static void sendHtmlAndCssUpdate(WebDriver driver, String sessionId, List<String> logs) {
        if (webSocketHandler != null) {
            try {
                String formattedHtml = HtmlCssFormatter.formatHtml(driver.getPageSource());
                String formattedCss = HtmlCssFormatter.formatCss(getCurrentPageCss(driver));


                logs.add("실시간 HTML & CSS 전송");
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
