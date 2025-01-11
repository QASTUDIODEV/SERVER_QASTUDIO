package qastudio.backend.domain.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qastudio.backend.domain.test.dto.response.ErrorResponse;
import qastudio.backend.domain.test.service.ErrorQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/tests/{testId}/errors")
public class ErrorController {

    private final ErrorQueryService errorQueryService;

    @Operation(
            summary = "오류 조회 API",
            description = "오류의 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TEST404", description = "존재하지 않는 테스트입니다.")
    })
    @GetMapping("")
    public ApiResponse<ErrorResponse.Error> getError(@PathVariable("testId") Long testId) {
        ErrorResponse.Error errorDetail = errorQueryService.getError(testId);
        return ApiResponse.onSuccess(errorDetail);
    }
}
