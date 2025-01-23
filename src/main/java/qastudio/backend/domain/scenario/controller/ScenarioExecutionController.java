package qastudio.backend.domain.scenario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.scenario.dto.request.EnvironmentRequest;
import qastudio.backend.domain.scenario.dto.request.ScenarioExecutionRequest;
import qastudio.backend.domain.scenario.dto.response.ExecutionResultResponse;
import qastudio.backend.domain.scenario.service.ScenarioExecutionService;
import qastudio.backend.domain.selenium.dto.response.SeleniumExecutionResponse;
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
    public ApiResponse<SeleniumExecutionResponse> executeScenario(
            @PathVariable Long scenarioId,
            @RequestBody @Valid ScenarioExecutionRequest request) {
        SeleniumExecutionResponse response = scenarioExecutionService.executeScenario(request);
        return ApiResponse.onSuccess(response);
    }
}
