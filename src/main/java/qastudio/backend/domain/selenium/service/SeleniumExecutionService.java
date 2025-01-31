package qastudio.backend.domain.selenium.service;


import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;

public interface SeleniumExecutionService {
    SeleniumExecutionResponse executeTest(String sessionId, Long userId, SeleniumExecutionRequest request);
}
