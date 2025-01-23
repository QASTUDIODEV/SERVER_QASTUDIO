package qastudio.backend.domain.selenium.util;

import qastudio.backend.domain.selenium.entity.enums.LocatorType;
import qastudio.backend.domain.selenium.entity.enums.ActionType;

import java.util.*;

public class LocatorActionMapper {
    private static final Map<LocatorType, Set<ActionType>> locatorActionMap = new HashMap<>();

    static {
        Set<ActionType> commonActions = Set.of(
                ActionType.CLICK,
                ActionType.SEND_KEYS,
                ActionType.CLEAR,
                ActionType.GET_ATTRIBUTE,
                ActionType.GET_TEXT,
                ActionType.IS_DISPLAYED,
                ActionType.IS_ENABLED,
                ActionType.IS_SELECTED
        );

        locatorActionMap.put(LocatorType.ID, commonActions);
        locatorActionMap.put(LocatorType.NAME, commonActions);
        locatorActionMap.put(LocatorType.CLASS_NAME, commonActions);
        locatorActionMap.put(LocatorType.TAG_NAME, commonActions);

        locatorActionMap.put(LocatorType.LINK_TEXT, Set.of(
                ActionType.CLICK,
                ActionType.GET_ATTRIBUTE,
                ActionType.GET_TEXT,
                ActionType.IS_DISPLAYED,
                ActionType.IS_ENABLED
        ));

        locatorActionMap.put(LocatorType.PARTIAL_LINK_TEXT, Set.of(
                ActionType.CLICK,
                ActionType.GET_ATTRIBUTE,
                ActionType.GET_TEXT,
                ActionType.IS_DISPLAYED,
                ActionType.IS_ENABLED
        ));

        locatorActionMap.put(LocatorType.CSS_SELECTOR, commonActions);
        locatorActionMap.put(LocatorType.XPATH, commonActions);
    }

    public static boolean isActionSupported(LocatorType locator, ActionType action) {
        return locatorActionMap.getOrDefault(locator, Collections.emptySet()).contains(action);
    }
}
