package qastudio.backend.domain.selenium.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeleniumExecutionServiceImpl implements SeleniumExecutionService {

    @Override
    public SeleniumExecutionResponse executeTest(SeleniumExecutionRequest request) {
        WebDriver driver = new ChromeDriver();
        List<String> executionLogs = new ArrayList<>();

        try {
            driver.get(request.getTargetUrl());
            executionLogs.add("URL 접근: " + request.getTargetUrl());

            for (SeleniumExecutionRequest.Action action : request.getActions()) {
                executionLogs.add("➡ Step " + action.getStep() + ": " + action.getActionName());
                performAction(driver, action, executionLogs);
            }

            executionLogs.add("✅ 테스트 완료");
            return new SeleniumExecutionResponse("SUCCESS", executionLogs);

        } catch (Exception e) {
            executionLogs.add("❌ 실행 중 오류 발생: " + e.getMessage());
            return new SeleniumExecutionResponse("FAILURE", executionLogs);
        } finally {
//            driver.quit();
        }
    }
    private void performAction(WebDriver driver, SeleniumExecutionRequest.Action action, List<String> logs) {
        try {
            SeleniumExecutionRequest.Element element = action.getElement();
            LocatorType locatorType = LocatorType.fromString(element.getLocator().getStrategy());
            By locator = getByLocator(locatorType, element.getLocator().getValue());

            WebElement webElement = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.presenceOfElementLocated(locator));

            ActionType actionType = ActionType.fromString(element.getAction().getType());

            if (!qastudio.backend.domain.selenium.util.LocatorActionMapper.isActionSupported(locatorType, actionType)) {
                logs.add("⚠️ 지원되지 않는 액션: " + actionType.getValue() + " on " + locatorType.getValue());
                return;
            }

            switch (actionType) {
                case CLICK:
                    webElement.click();
                    logs.add("✅ 클릭 실행: " + element.getName());
                    break;
                case SEND_KEYS:
                    webElement.sendKeys(element.getAction().getValue());
                    logs.add("✅ 입력: " + element.getAction().getValue() + " → " + element.getName());
                    break;
                case IS_SELECTED:
                    boolean selected = webElement.isSelected();
                    logs.add("✅ 체크박스 선택 상태: " + selected);
                    break;
                default:
                    logs.add("⚠️ 알 수 없는 액션: " + actionType.getValue());
            }
        } catch (Exception e) {
            logs.add("❌ 요소 찾기 실패 또는 실행 오류: " + action.getElement().getName() + " - 오류: " + e.getMessage());
        }
    }

    private By getByLocator(LocatorType locatorType, String value) {
        switch (locatorType) {
            case ID:
                return By.id(value);
            case NAME:
                return By.name(value);
            case CLASS_NAME:
                return By.className(value);
            case TAG_NAME:
                return By.tagName(value);
            case LINK_TEXT:
                return By.linkText(value);
            case PARTIAL_LINK_TEXT:
                return By.partialLinkText(value);
            case CSS_SELECTOR:
                return By.cssSelector(value);
            case XPATH:
                return By.xpath(value);
            default:
                throw new IllegalArgumentException("지원되지 않는 Locator 유형: " + locatorType);
        }
    }
}
