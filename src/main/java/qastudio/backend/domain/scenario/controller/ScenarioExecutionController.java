package qastudio.backend.domain.scenario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.scenario.dto.request.BaseUrlRequest;
import qastudio.backend.domain.scenario.service.ScenarioQueryService;
import qastudio.backend.domain.selenium.dto.request.SeleniumExecutionRequest;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
import qastudio.backend.domain.selenium.service.SeleniumExecutionService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.global.handler.annotation.Auth;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/scenarios")
public class ScenarioExecutionController {

    private final ScenarioQueryService scenarioQueryService;
    private final SeleniumExecutionService seleniumExecutionService;

    @Operation(summary = "시나리오 실행 API | 준", description = "시나리오를 실행하고 결과를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "시나리오를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/{scenarioId}/sessions/{sessionId}/execute")
    public ResponseEntity<ApiResponse<SeleniumExecutionResponse>> executeScenario(
            @PathVariable String sessionId,
            @PathVariable Long scenarioId,
            @Auth Long userId,
            @RequestBody BaseUrlRequest request
    ) {
        if (request.getBaseUrl() == null || request.getBaseUrl().trim().isEmpty()) {
            throw new IllegalArgumentException("Base URL이 비어 있습니다. 올바른 URL을 입력하세요.");
        }

        SeleniumExecutionRequest executionRequest = scenarioQueryService.getExecutionRequestByScenarioId(scenarioId, userId, request.getBaseUrl());
        SeleniumExecutionResponse response = seleniumExecutionService.executeTest(sessionId, userId, executionRequest);

        return ResponseEntity.ok(ApiResponse.onSuccess(response));
    }
}
