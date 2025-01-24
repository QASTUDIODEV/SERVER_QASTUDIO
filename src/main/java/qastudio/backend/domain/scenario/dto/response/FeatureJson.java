package qastudio.backend.domain.scenario.dto.response;

import lombok.Getter;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;

@Getter
public class FeatureJson {
    private SeleniumExecutionRequest.Locator locator;
    private SeleniumExecutionRequest.Action action;
}
