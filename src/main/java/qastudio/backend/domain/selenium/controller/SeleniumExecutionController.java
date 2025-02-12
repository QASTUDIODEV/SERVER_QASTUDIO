package qastudio.backend.domain.selenium.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.selenium.dto.request.FetchPageSourceRequest;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.service.SeleniumExecutionService;
import qastudio.backend.global.handler.annotation.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/selenium")
public class SeleniumExecutionController {

    private final SeleniumExecutionService seleniumExecutionService;

    @Operation(summary = "셀레니움 테스트 실행 |by 준", description = "입력된 액션들을 기반으로 Selenium 테스트를 수행합니다.(백엔드 테스트용으로 사용)")
    @PostMapping("/execute")
    public SeleniumExecutionResponse executeSeleniumTest(
            @RequestParam String sessionId,
            @Auth Long userId,
            @RequestBody SeleniumExecutionRequest request) {
        return seleniumExecutionService.executeTest(sessionId, userId, request);
    }

    @Operation(summary = "페이지 소스 가져오기 |by 준", description = "입력된 URL을 기반으로 페이지의 HTML과 CSS를 가져옵니다.")
    @PostMapping("/fetchPageSource")
    public SeleniumExecutionResponse fetchPageSource(
            @Auth Long userId,
            @RequestBody FetchPageSourceRequest request) {
        return seleniumExecutionService.fetchPageSource(userId, request.getTargetUrl());
    }
}
