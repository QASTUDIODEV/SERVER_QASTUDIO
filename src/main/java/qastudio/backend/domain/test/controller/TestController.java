package qastudio.backend.domain.test.controller;

import com.querydsl.core.Tuple;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.converter.TestConverter;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;
import qastudio.backend.domain.test.service.TestQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/projects/{projectId}/tests")
public class TestController {

    private final TestQueryService testQueryService;

    @Operation(
            summary = "테스트 리스트 조회 API | by 제로",
            description = "테스트 리스트를 조회하고, 테스트 이름을 기준으로 검색합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.")
    })
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호, 0번이 1 페이지입니다."),
            @Parameter(name = "testName", description = "검색할 테스트 이름"),
            @Parameter(name = "date", description = "날짜별 정렬 (YYYY-MM-DD)"),
            @Parameter(name = "pageName", description = "페이지별 정렬"),
            @Parameter(name = "state", description = "성취 여부별 정렬")
    })
    @GetMapping("")
    public ApiResponse<TestResponse.TestList> getTestList(
            @PathVariable("projectId") Long projectId,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "testName", required = false) String testName,
            @RequestParam(name = "date", required = false) LocalDate date,
            @RequestParam(name = "pageName", required = false) String pageName,
            @RequestParam(name = "state", required = false) State state) {
        Page<Test> testList = testQueryService.getTestList(projectId, page, testName, date, pageName, state);
        return ApiResponse.onSuccess(TestConverter.toTestList(testList));
    }

    @Operation(
            summary = "테스트 통계 조회 API | by 제로",
            description = "테스트의 통계를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.")
    })
    @GetMapping("/statistics")
    public ApiResponse<TestResponse.TestStatistics> getTestStatistics(@PathVariable("projectId") Long projectId) {
        Project project = testQueryService.getTestStatistics(projectId);

        List<Tuple> testCounts = testQueryService.getTestCounts(projectId);
        Double successRate = testQueryService.getSuccessRate(testCounts);
        Double failRate = testQueryService.getFailRate(testCounts);

        return ApiResponse.onSuccess(TestConverter.toTestStatistics(project, successRate, failRate));
    }
}
