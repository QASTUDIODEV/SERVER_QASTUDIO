package qastudio.backend.domain.test.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.enums.State;
import qastudio.backend.domain.test.service.TestQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/{projectId}/tests")
public class TestController {

    private final TestQueryService testQueryService;

    @Operation(
            summary = "테스트 리스트 조회 API",
            description = "테스트 리스트를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.")
    })
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호, 0번이 1 페이지입니다."),
            @Parameter(name = "date", description = "날짜별 정렬"),
            @Parameter(name = "pageName", description = "페이지별 정렬"),
            @Parameter(name = "state", description = "성취 여부별 정렬")
    })
    @GetMapping("")
    public ApiResponse<TestResponse.TestList> getTestList(
            @PathVariable("projectId") Long projectId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "pageName", required = false) String pageName,
            @RequestParam(name = "state", required = false) State state) {
        TestResponse.TestList testList = testQueryService.getTestList(projectId, page, date, pageName, state);
        return ApiResponse.onSuccess(testList);
    }

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

    @Operation(
            summary = "테스트 검색 API",
            description = "테스트를 Name(테스트 이름) 기준으로 검색합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TEST404", description = "존재하지 않는 테스트입니다.")
    })
    @Parameters({
            @Parameter(name = "testName", description = "검색할 테스트 이름"),
            @Parameter(name = "page", description = "페이지 번호, 0번이 1 페이지입니다."),
            @Parameter(name = "date", description = "날짜별 정렬"),
            @Parameter(name = "pageName", description = "페이지별 정렬"),
            @Parameter(name = "state", description = "성취 여부별 정렬")
    })
    @GetMapping("/search")
    public ApiResponse<TestResponse.TestList> searchTestsByTestName(
            @PathVariable("projectId") Long projectId,
            @RequestParam(name = "testName", required = false) String testName,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "pageName", required = false) String pageName,
            @RequestParam(name = "state", required = false) State state) {
        TestResponse.TestList testList = testQueryService.searchTestsByTestName(projectId, testName, page, date, pageName, state);
        return ApiResponse.onSuccess(testList);
    }
}
