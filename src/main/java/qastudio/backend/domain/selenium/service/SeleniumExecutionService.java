package qastudio.backend.domain.selenium.service;


import qastudio.backend.domain.selenium.dto.request.CustomExecutionRequest;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;

public interface SeleniumExecutionService {
    SeleniumExecutionResponse fetchPageSource(Long userId, String targetUrl);
    SeleniumExecutionResponse executeTest(String sessionId, Long userId, SeleniumExecutionRequest request);
    SeleniumExecutionResponse executeRecordActions(CustomExecutionRequest request);
}
