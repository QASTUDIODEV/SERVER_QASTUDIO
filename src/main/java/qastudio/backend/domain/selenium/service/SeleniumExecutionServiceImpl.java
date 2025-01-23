package qastudio.backend.domain.selenium.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.util.SeleniumActionExecutor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeleniumExecutionServiceImpl implements SeleniumExecutionService {

    @Override
    public SeleniumExecutionResponse executeTest(SeleniumExecutionRequest request) {
        WebDriver driver = new ChromeDriver();
        List<String> executionLogs = new ArrayList<>();
        try {
            driver.get(request.getTargetUrl());
            executionLogs.add("🌍 URL 접근: " + request.getTargetUrl());

            for (SeleniumExecutionRequest.Action action : request.getActions()) {
                executionLogs.add("➡ Step " + action.getStep() + ": " + action.getActionName());
                SeleniumActionExecutor.performAction(driver, action, executionLogs);
            }
            executionLogs.add("✅ 테스트 완료");
            return new SeleniumExecutionResponse("SUCCESS", executionLogs);
        } catch (Exception e) {
            executionLogs.add("❌ 실행 중 오류 발생: " + e.getMessage());
            return new SeleniumExecutionResponse("FAILURE", executionLogs);
        } finally {
//            driver.quit();
        }
    }
}
