package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import qastudio.backend.domain.selenium.dto.ActionExecutionResult;
import qastudio.backend.domain.selenium.dto.request.CustomExecutionRequest;
import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;

public class CustomActionExecutor {

    private CustomActionExecutor() {}

    public static ActionExecutionResult performRecordAction(WebDriver driver, CustomExecutionRequest.ActionDetail actionDetail, List<String> logs) {
        WebElement webElement = null;
        try {
            webElement = findElementSafely(driver, actionDetail);
//            Thread.sleep(3000);
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(driver1 -> System.nanoTime() + 2_000_000_000L < System.nanoTime());


            if (webElement == null) {
                throw new NoSuchElementException("Locator not found: " + actionDetail.getLocator().getValue());
            }

            // 액션 변환
            String actionTypeString = actionDetail.getAction().getType();
            if ("navigate".equalsIgnoreCase(actionTypeString) || "click".equalsIgnoreCase(actionTypeString)) {
                actionTypeString = "click";
            } else if ("fill text".equalsIgnoreCase(actionTypeString)) {
                actionTypeString = "send_keys";
            }
            ActionType actionType = ActionType.fromString(actionTypeString);

            LocatorActionValidator.validate(LocatorType.fromString(actionDetail.getLocator().getStrategy()), actionType);
            ActionExecutor.executeAction(driver, webElement, actionType, actionDetail, logs);

            return new ActionExecutionResult(1, null, null, null);

        } catch (Exception e) {
            logs.add("❌ 요소 찾기 실패 또는 실행 오류: " + actionDetail.getActionDescription() + " - 오류: " + e.getMessage());
            return new ActionExecutionResult(0, 500, e.getMessage(), null);
        } finally {
            webElement = null; // 메모리 해제
            System.gc(); // 가비지 컬렉션 실행
        }
    }

    private static WebElement findElementSafely(WebDriver driver, CustomExecutionRequest.ActionDetail actionDetail) {
        try {
            LocatorType locatorType = LocatorType.fromString(actionDetail.getLocator().getStrategy());
            return new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(LocatorUtils.getByLocator(locatorType, actionDetail.getLocator().getValue())));
        } catch (TimeoutException e) {
            return null;
        }
    }
}