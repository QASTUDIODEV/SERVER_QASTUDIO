package qastudio.backend.domain.selenium.entity.enums;

public enum LocatorType {
    ID("id"),
    NAME("name"),
    CLASS_NAME("class_name"),
    TAG_NAME("tag_name"),
    LINK_TEXT("link_text"),
    PARTIAL_LINK_TEXT("partial_link_text"),
    CSS_SELECTOR("css_selector"),
    XPATH("xpath");

    private final String value;

    LocatorType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static LocatorType fromString(String text) {
        for (LocatorType type : LocatorType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지원되지 않는 Locator 유형: " + text);
    }
}
