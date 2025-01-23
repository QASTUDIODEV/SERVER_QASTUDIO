package qastudio.backend.domain.scenario.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import qastudio.backend.domain.selenium.entity.enums.ActionType;
import qastudio.backend.domain.selenium.entity.enums.LocatorType;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScenarioExecutionRequest {
    private String targetUrl;
    private List<Action> actions;

    @Getter
    @NoArgsConstructor
    public static class Action {
        private String actionName;
        private Integer step;
        private Element element;
    }

    @Getter
    @NoArgsConstructor
    public static class Element {
        private String name;
        private String type;
        private Locator locator;
        private ActionDetail action;
    }

    @Getter
    @NoArgsConstructor
    public static class Locator {
        private LocatorType strategy;
        private String value;
    }

    @Getter
    @NoArgsConstructor
    public static class ActionDetail {
        private ActionType type;
        private String value;
    }
}
