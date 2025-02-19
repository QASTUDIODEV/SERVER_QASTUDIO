package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.WebElement;
import qastudio.backend.domain.selenium.dto.request.CustomExecutionRequest;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.entity.enums.ActionType;

import java.util.List;

public class ActionExecutor {

    private ActionExecutor() {}

    public static void executeAction(WebElement webElement, ActionType actionType, SeleniumExecutionRequest.ActionDetail actionDetail, List<String> logs) {
        switch (actionType) {
            case CLICK:
                webElement.click();
                logs.add("✅ 클릭 실행: " + actionDetail.getActionDescription());
                break;
            case SEND_KEYS:
                webElement.sendKeys(actionDetail.getAction().getValue());
                logs.add("✅ 입력: " + actionDetail.getAction().getValue() + " → " + actionDetail.getActionDescription());
                break;
            case CLEAR:
                webElement.clear();
                logs.add("✅ 입력 필드 초기화: " + actionDetail.getActionDescription());
                break;
            case GET_ATTRIBUTE:
                String attributeValue = webElement.getAttribute(actionDetail.getAction().getValue());
                logs.add("✅ 속성 값 가져오기: " + actionDetail.getAction().getValue() + " = " + attributeValue);
                break;
            case TEXT:
                String text = webElement.getText();
                logs.add("✅ 텍스트 가져오기: " + text);
                break;
            case IS_DISPLAYED:
                boolean isDisplayed = webElement.isDisplayed();
                logs.add("✅ 요소 표시 여부: " + isDisplayed);
                break;
            case IS_ENABLED:
                boolean isEnabled = webElement.isEnabled();
                logs.add("✅ 요소 활성화 여부: " + isEnabled);
                break;
            case IS_SELECTED:
                boolean isSelected = webElement.isSelected();
                logs.add("✅ 체크박스 선택 여부: " + isSelected);
                break;
            case  WAIT:
                try {
                    Thread.sleep(3000);
                    logs.add("✅ 대기: 3초");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logs.add("❌ 대기 중단: " + e.getMessage());
                }
                break;
            default:
                logs.add("⚠️ 지원되지 않는 액션: " + actionType.getValue());
                throw new UnsupportedOperationException("지원되지 않는 액션: " + actionType.getValue());
        }
    }


    public static void executeAction(WebElement webElement, ActionType actionType, CustomExecutionRequest.ActionDetail actionDetail, List<String> logs) {
        switch (actionType) {
            case CLICK:
                webElement.click();
                logs.add("✅ 클릭 실행: " + actionDetail.getActionDescription());
                break;
            case SEND_KEYS:
                webElement.sendKeys(actionDetail.getAction().getValue());
                logs.add("✅ 입력: " + actionDetail.getAction().getValue() + " → " + actionDetail.getActionDescription());
                break;
            case CLEAR:
                webElement.clear();
                logs.add("✅ 입력 필드 초기화: " + actionDetail.getActionDescription());
                break;
            case GET_ATTRIBUTE:
                String attributeValue = webElement.getAttribute(actionDetail.getAction().getValue());
                logs.add("✅ 속성 값 가져오기: " + actionDetail.getAction().getValue() + " = " + attributeValue);
                break;
            case TEXT:
                String text = webElement.getText();
                logs.add("✅ 텍스트 가져오기: " + text);
                break;
            case IS_DISPLAYED:
                boolean isDisplayed = webElement.isDisplayed();
                logs.add("✅ 요소 표시 여부: " + isDisplayed);
                break;
            case IS_ENABLED:
                boolean isEnabled = webElement.isEnabled();
                logs.add("✅ 요소 활성화 여부: " + isEnabled);
                break;
            case IS_SELECTED:
                boolean isSelected = webElement.isSelected();
                logs.add("✅ 체크박스 선택 여부: " + isSelected);
                break;
            case WAIT:
                try {
                    Thread.sleep(3000);
                    logs.add("✅ 대기: 3초");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logs.add("❌ 대기 중단: " + e.getMessage());
                }
                break;
            default:
                logs.add("⚠️ 지원되지 않는 액션: " + actionType.getValue());
                throw new UnsupportedOperationException("지원되지 않는 액션: " + actionType.getValue());
        }
    }
}
