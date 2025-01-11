package qastudio.backend.domain.scenario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.scenario.dto.request.EnvironmentRequest;
import qastudio.backend.domain.scenario.dto.response.ExecutionResultResponse;
import qastudio.backend.domain.scenario.service.ScenarioExecutionService;
import qastudio.backend.global.apiPayload.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/scenarios")
public class ScenarioExecutionController {

    private final ScenarioExecutionService scenarioExecutionService;

    @Operation(summary = "시나리오 실행 API", description = "시나리오를 실행하고 결과를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "시나리오를 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/{scenarioId}/execute")
    public ApiResponse<ExecutionResultResponse> executeScenario(
            @PathVariable Long scenarioId,
            @RequestBody @Valid EnvironmentRequest request) {
        ExecutionResultResponse result = scenarioExecutionService.executeScenario(scenarioId, request);
        return ApiResponse.onSuccess(result);
    }

//    @Operation(summary = "시나리오 실행 중단 API", description = "실행 중인 시나리오를 중단합니다.")
//    @PostMapping("/{executionId}/stop")
//    public ApiResponse<Void> stopScenario(@PathVariable Long executionId) {
//        scenarioExecutionService.stopScenario(executionId);
//        return ApiResponse.onSuccess(null);
//    }
}
