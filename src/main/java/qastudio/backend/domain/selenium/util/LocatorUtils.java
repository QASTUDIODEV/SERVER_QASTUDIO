package qastudio.backend.domain.selenium.util;

import org.openqa.selenium.By;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;

public class LocatorUtils {

    private LocatorUtils() {}

    public static By getByLocator(LocatorType locatorType, String value) {
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
