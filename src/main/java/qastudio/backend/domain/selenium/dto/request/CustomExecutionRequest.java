package qastudio.backend.domain.selenium.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomExecutionRequest {
    @Schema(description = "Target URL for the Selenium actions", example = "http://example.com")
    private String url;

    @Schema(description = "List of actions to be performed")
    private List<ActionDetail> actions;

    @Getter
    @Setter
    public static class ActionDetail{
        @Schema(description = "Description of the action", example = "Enter Email")
        private String actionDescription;

        @Schema(description = "Step number of the action", example = "1")
        private Integer step;

        @Schema(description = "Type of the action", example = "input")
        private String actionType;

        @Schema(description = "Locator for the element")
        private Locator locator;

        @Schema(description = "Details of the action to be performed")
        private Action action;
    }

    @Getter
    @Setter
    public static class Locator {
        @Schema(description = "Strategy to locate the element", example = "id")
        private String strategy;

        @Schema(description = "Value of the locator", example = "email")
        private String value;
    }

    @Getter
    @Setter
    public static class Action {
        @Schema(description = "Type of the action", example = "send_keys")
        private String type;

        @Schema(description = "Value to be sent in the action", example = "testuser@example.com")
        private String value;
    }
}