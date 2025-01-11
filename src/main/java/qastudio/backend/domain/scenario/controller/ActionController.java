package qastudio.backend.domain.scenario.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.scenario.dto.request.ActionUpdateRequest;
import qastudio.backend.domain.scenario.dto.response.ActionResponse;
import qastudio.backend.domain.scenario.service.ActionCommandService;
import qastudio.backend.global.apiPayload.ApiResponse;

import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/actions")
public class ActionController {

    private final ActionCommandService actionCommandService;

    @Operation(
            summary = "액션 업데이트 API",
            description = "선택 방법과 행동 종류를 포함한 액션의 일부 정보를 업데이트합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON404", description = "액션을 찾을 수 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PatchMapping("/{actionId}")
    public ApiResponse<ActionResponse> updateAction(
            @PathVariable Long actionId,
            @RequestBody @Valid ActionUpdateRequest request) {
        ActionResponse response = actionCommandService.updateAction(actionId, request);
        return ApiResponse.onSuccess(response);
    }
}