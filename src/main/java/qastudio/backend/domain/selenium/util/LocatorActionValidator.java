package qastudio.backend.domain.selenium.util;

import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;

import java.util.Map;
import java.util.Set;

public class LocatorActionValidator {
    private static final Map<LocatorType, Set<ActionType>> supportedActions = Map.of(
            LocatorType.ID, Set.of(ActionType.CLICK, ActionType.SEND_KEYS, ActionType.CLEAR, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED, ActionType.IS_SELECTED),
            LocatorType.NAME, Set.of(ActionType.CLICK, ActionType.SEND_KEYS, ActionType.CLEAR, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED, ActionType.IS_SELECTED),
            LocatorType.CLASS_NAME, Set.of(ActionType.CLICK, ActionType.SEND_KEYS, ActionType.CLEAR, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED, ActionType.IS_SELECTED),
            LocatorType.TAG_NAME, Set.of(ActionType.CLICK, ActionType.SEND_KEYS, ActionType.CLEAR, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED, ActionType.IS_SELECTED),
            LocatorType.LINK_TEXT, Set.of(ActionType.CLICK, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED),
            LocatorType.PARTIAL_LINK_TEXT, Set.of(ActionType.CLICK, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED),
            LocatorType.CSS_SELECTOR, Set.of(ActionType.CLICK, ActionType.SEND_KEYS, ActionType.CLEAR, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED, ActionType.IS_SELECTED),
            LocatorType.XPATH, Set.of(ActionType.CLICK, ActionType.SEND_KEYS, ActionType.CLEAR, ActionType.GET_ATTRIBUTE, ActionType.TEXT, ActionType.IS_DISPLAYED, ActionType.IS_ENABLED, ActionType.IS_SELECTED)
    );

    public static void validate(LocatorType locatorType, ActionType actionType) {
        if (!supportedActions.containsKey(locatorType) || !supportedActions.get(locatorType).contains(actionType)) {
            throw new UnsupportedOperationException("❌ 로케이터 '" + locatorType.getValue() + "'에서는 액션 '" + actionType.getValue() + "'을 지원하지 않습니다.");
        }
    }
}
