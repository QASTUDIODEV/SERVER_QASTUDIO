package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.*;
import org.openqa.selenium.io.FileHandler;
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
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Base64;
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
            WebElement webElement = findElementSafely(driver, actionDetail);
            if (webElement == null) {
                throw new NoSuchElementException("Locator not found: " + actionDetail.getLocator().getValue());
            }

            ActionType actionType = ActionType.fromString(actionDetail.getAction().getType());
            LocatorActionValidator.validate(LocatorType.fromString(actionDetail.getLocator().getStrategy()), actionType);
            ActionExecutor.executeAction(webElement, actionType, actionDetail, logs);

            webSocketHandler.sendImageBinaryWithMetadata(sessionId, driver);
//            sendImageUpdate(driver, sessionId, logs);
            return new ActionExecutionResult(1, null, null, null);

        } catch (Exception e) {
            logs.add("❌ 요소 찾기 실패 또는 실행 오류: " + actionDetail.getActionDescription() + " - 오류: " + e.getMessage());
            // 브라우저 화면 캡처 및 S3 업로드
            String imageUrl = captureScreenshotAndUpload(driver);
            // 오류 정보만 반환 (데이터 저장은 executeTest()에서 수행)
            return new ActionExecutionResult(0, 500, e.getMessage(), imageUrl);
        } finally {
            if (webSocketHandler != null) {
                webSocketHandler.closeSession(sessionId);
            }
        }
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

    private static void sendImageUpdate(WebDriver driver, String sessionId, List<String> logs) {
        if (webSocketHandler != null) {
            try {
                String base64Image = captureScreenshotAsBase64(driver);
                if (base64Image != null) {
                    logs.add("실시간 스크린샷 전송");
                    webSocketHandler.sendImage(sessionId, base64Image);
                } else {
                    logs.add("❌ 스크린샷 캡처 실패");
                }
            } catch (Exception e) {
                logs.add("❌ 이미지 전송 실패: " + e.getMessage());
            }
        }
    }
    private static String captureScreenshotAsBase64(WebDriver driver) {
        try {
            // PNG 대신 JPG로 변환하여 압축률 증가
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage bufferedImage = ImageIO.read(screenshotFile);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream); // PNG 대신 JPG로 저장

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
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

    private static void sendHtmlAndCssUpdate(WebDriver driver, String sessionId, List<String> logs) {
        if (webSocketHandler != null) {
            try {
                String formattedHtml = "`" + HtmlCssFormatter.formatHtml(driver.getPageSource()) + "`";
                String formattedCss = "`" + HtmlCssFormatter.formatCss(getCurrentPageCss(driver)) + "`";


                logs.add("실시간 HTML & CSS 전송");
                webSocketHandler.sendHtmlAndCss(sessionId, formattedHtml, formattedCss);
            } catch (Exception e) {
                logs.add("❌ HTML & CSS 전송 실패: " + e.getMessage());
            }
        }
    }

    public static String getCurrentPageCss(WebDriver driver) {
        String rawCss = extractCssFromPage(driver);
        return processCss(rawCss);
    }

    /**
     * 웹 페이지에서 CSS를 추출
     */
    private static String extractCssFromPage(WebDriver driver) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "let css = ''; " +
                        "document.querySelectorAll('style').forEach(style => { " +
                        "    css += style.innerHTML + '\\n'; " +
                        "}); " +
                        "document.querySelectorAll('*').forEach(element => { " +
                        "    let computedStyle = window.getComputedStyle(element); " +
                        "    let styles = ''; " +
                        "    for (let i = 0; i < computedStyle.length; i++) { " +
                        "        styles += computedStyle[i] + ':' + computedStyle.getPropertyValue(computedStyle[i]) + ';'; " +
                        "    } " +
                        "    if (styles) { css += element.tagName.toLowerCase() + '{' + styles + '}\\n'; } " +
                        "}); " +
                        "return css;"
        );
    }

    /**
     * 중복된 CSS 속성을 제거하고 최적화
     */
    private static String processCss(String css) {
        if (css == null || css.isEmpty()) {
            return "";
        }

        Map<String, Map<String, String>> cssMap = new HashMap<>();

        for (String rule : css.split("\\n")) {
            if (!rule.contains("{") || !rule.contains("}")) {
                continue;
            }

            String tag = rule.substring(0, rule.indexOf('{')).trim();
            String properties = rule.substring(rule.indexOf('{') + 1, rule.indexOf('}')).trim();

            cssMap.putIfAbsent(tag, new HashMap<>());
            Map<String, String> propertyMap = cssMap.get(tag);

            for (String property : properties.split(";")) {
                String[] keyValue = property.split(":");
                if (keyValue.length == 2) {
                    propertyMap.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }

        return generateCssString(cssMap);
    }

    /**
     * 정리된 CSS 데이터를 문자열로
     */
    private static String generateCssString(Map<String, Map<String, String>> cssMap) {
        StringBuilder processedCss = new StringBuilder();

        for (Map.Entry<String, Map<String, String>> entry : cssMap.entrySet()) {
            processedCss.append(entry.getKey()).append(" { ");
            for (Map.Entry<String, String> property : entry.getValue().entrySet()) {
                processedCss.append(property.getKey()).append(": ").append(property.getValue()).append("; ");
            }
            processedCss.append("}\n");
        }

        return processedCss.toString();
    }
}
