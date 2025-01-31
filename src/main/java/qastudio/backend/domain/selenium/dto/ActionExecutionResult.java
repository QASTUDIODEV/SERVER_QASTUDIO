package qastudio.backend.domain.selenium.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionExecutionResult {
    private final boolean success;
    private final Integer errorCode;
    private final String errorMessage;
    private final String errorImage;
}
