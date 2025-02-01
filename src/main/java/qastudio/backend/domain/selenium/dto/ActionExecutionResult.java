package qastudio.backend.domain.selenium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionExecutionResult {
    private final int executedActions;
    private final Integer errorCode;
    private final String errorMessage;
    private final String errorImage;

    public boolean hasError() {
        return errorCode != null;
    }
}
