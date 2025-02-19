package qastudio.backend.domain.selenium.entity.enums;

public enum ActionType {
    CLICK("click"),
    SEND_KEYS("send_keys"),
    CLEAR("clear"),
    GET_ATTRIBUTE("get_attribute"),
    TEXT("text"),
    IS_DISPLAYED("is_displayed"),
    IS_ENABLED("is_enabled"),
    IS_SELECTED("is_selected"),
    WAIT("wait"); // Add WAIT here

    private final String value;

    ActionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ActionType fromString(String text) {
        for (ActionType action : ActionType.values()) {
            if (action.value.equalsIgnoreCase(text)) {
                return action;
            }
        }
        throw new IllegalArgumentException("지원되지 않는 액션 유형: " + text);
    }
}
