package qastudio.backend.domain.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.service.TestQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/{projectId}/tests")
public class TestController {

    private final TestQueryService testQueryService;

    @Operation(
            summary = "테스트 통계 조회 API",
            description = "테스트의 통계를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.")
    })
    @GetMapping("/statistics")
    public ApiResponse<TestResponse.TestStatistics> getTestStatistics(@PathVariable("projectId") Long projectId) {
        TestResponse.TestStatistics testStatistics = testQueryService.getTestStatistics(projectId);
        return ApiResponse.onSuccess(testStatistics);
    }

}
