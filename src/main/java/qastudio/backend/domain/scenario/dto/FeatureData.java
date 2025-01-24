package qastudio.backend.domain.scenario.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

@Getter
@NoArgsConstructor
public class FeatureData {
    private SeleniumExecutionRequest.Locator locator;
    private SeleniumExecutionRequest.Action action;
}
