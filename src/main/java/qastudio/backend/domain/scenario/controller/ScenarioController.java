package qastudio.backend.domain.scenario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.dto.response.CharacterResponse;
import qastudio.backend.domain.project.service.CharacterQueryService;
import qastudio.backend.domain.scenario.dto.request.ScenarioRequest;
import qastudio.backend.domain.scenario.dto.response.ScenarioDetailResponse;
import qastudio.backend.domain.scenario.dto.response.ScenarioListDetailResponse;
import qastudio.backend.domain.scenario.dto.response.ScenarioResponse;
import qastudio.backend.domain.scenario.service.ActionCommandService;
import qastudio.backend.domain.scenario.service.ScenarioCommandService;
import qastudio.backend.domain.scenario.service.ScenarioQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import jakarta.validation.Valid;
import qastudio.backend.global.handler.annotation.Auth;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/scenarios")
public class ScenarioController {

    private final ScenarioCommandService scenarioCommandService;
    private final ScenarioQueryService scenarioQueryService;
    private final ActionCommandService actionCommandService;
    private final CharacterQueryService characterQueryService;

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
            @RequestBody @Valid ScenarioRequest.CreateScenarioRequest request, @Auth Long userId) {

        // 시나리오 저장
        ScenarioResponse scenarioResponse = scenarioCommandService.createScenario(request, userId);

        // 액션 저장
        actionCommandService.createActionsForScenario(scenarioResponse.getId(), request.getActions());

        return ApiResponse.onSuccess(scenarioResponse);
    }
    

    @Operation(
            summary = "시나리오 수정 API | by 준",
            description = "새로운 시나리오를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "시나리오 수정 성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    // 시나리오 수정 API
    @PatchMapping("/{scenarioId}")
    public ApiResponse<ScenarioResponse> updateScenario(
            @PathVariable Long scenarioId,
            @RequestBody @Valid ScenarioRequest.UpdateScenarioRequest request,
            @Auth Long userId) {

        ScenarioResponse scenarioResponse = scenarioCommandService.updateScenario(scenarioId, request, userId);
        actionCommandService.deleteActionsForScenario(scenarioId);
        actionCommandService.createActionsForScenario(scenarioId, request.getActions());


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
    public ApiResponse<ScenarioDetailResponse> getScenarioDetail(@PathVariable Long scenarioId, @Auth Long userId) {
        ScenarioDetailResponse response = scenarioQueryService.getScenarioDetail(scenarioId, userId);
        return ApiResponse.onSuccess(response);
    }

    @Operation(
            summary = "캐릭터ID로 시나리오 조회 API | by 준",
            description = "프론트엔드 테스트용 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "시나리오를 찾을 수 없습니다.")
    })
    @GetMapping("/characters/{characterId}")
    public ApiResponse<ScenarioListDetailResponse> getScenarioListDetailByCharacter(@PathVariable Long characterId, @Auth Long userId) {
        CharacterResponse.ScenarioList scenarioList = characterQueryService.getScenarioList(characterId);

        List<ScenarioDetailResponse> scenarioDetails = scenarioList.getScenarioList().stream()
                .map(scenario -> scenarioQueryService.getScenarioDetail(scenario.getScenarioId(), userId))
                .collect(Collectors.toList());

        ScenarioListDetailResponse response = ScenarioListDetailResponse.builder()
                .characterId(characterId)
                .scenarios(scenarioDetails)
                .build();

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
