package qastudio.backend.domain.scenario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;
import qastudio.backend.domain.scenario.service.ActionCommandService;
import qastudio.backend.domain.scenario.service.ScenarioCommandService;
import qastudio.backend.domain.scenario.service.ScenarioQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/scenarios")
public class ScenarioController {

    private final ScenarioCommandService scenarioCommandService;
    private final ScenarioQueryService scenarioQueryService;
    private final ActionCommandService actionCommandService;

    @Operation(
            summary = "시나리오 생성 API | by 준",
            description = "새로운 시나리오를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "시나리오 생성 성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PostMapping
    public ApiResponse<ScenarioResponse> createScenario(
            @RequestBody @Valid ScenarioRequest.CreateScenarioRequest request) {
        ScenarioResponse scenarioResponse = scenarioCommandService.createScenario(request);
        actionCommandService.createActionsForScenario(scenarioResponse.getId(), request.getActions());

        return ApiResponse.onSuccess(scenarioResponse);
    }



    @Operation(
            summary = "시나리오 조회 API | by 준",
            description = "특정 시나리오의 정보를 반환하고, 해당 시나리오에 속한 액션 목록을 제공합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "시나리오를 찾을 수 없습니다.")
    })
    @GetMapping("/{scenarioId}")
    public ApiResponse<ScenarioDetailResponse> getScenario(@PathVariable Long scenarioId) {
        ScenarioDetailResponse response = scenarioQueryService.getScenarioById(scenarioId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(
            summary = "시나리오 삭제 API | by 챠리",
            description = "시나리오를 삭제합니다. 단일 시나리오를 삭제할 수도 있고, 여러 시나리오를 삭제할 수도 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "존재하지 않는 시나리오입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @DeleteMapping("")
    public ApiResponse<Void> deleteScenarios(@RequestBody ScenarioRequest.DeleteScenarios deleteScenarios) {
        scenarioCommandService.deleteScenarios(deleteScenarios.getScenarioIds());
        return ApiResponse.onSuccess(null);
    }
}
