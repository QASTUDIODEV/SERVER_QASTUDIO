package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.converter.PageConverter;
import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.dto.response.PageResponse;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.service.PageQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/pages")
public class PageController {

    private final PageQueryService pageQueryService;

    @Operation(
            summary = "페이지 생성 API",
            description = "페이지를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER400", description = "프로젝트에 속하지 않는 역할입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER404", description = "존재하지 않는 역할입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/{projectId}")
    public ApiResponse<PageResponse.PageSummary> createPage(@PathVariable("projectId") Long projectId, @RequestBody @Valid PageRequest.createPage createPage) {
        PageResponse.PageSummary pageSummary = pageQueryService.createPage(projectId, createPage);
        return ApiResponse.onSuccess(pageSummary);
    }


    @Operation(
            summary = "프로젝트 페이지 조회 API",
            description = "프로젝트에 해당하는 페이지를 모두 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/{projectId}")
    public ApiResponse<PageResponse.PageList> getAllPage(@PathVariable("projectId") Long projectId) {
        List<PageResponse.PageSummary> pages = pageQueryService.getAllPage(projectId);
        return ApiResponse.onSuccess(PageConverter.toPageList(pages));
    }

    @Operation(
            summary = "페이지 삭제 API",
            description = "페이지를 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PAGE404", description = "존재하지 않는 페이지입니다."),
    })
    @DeleteMapping("/{pageId}")
    public ApiResponse<Void>  deletePage(@PathVariable("pageId") Long pageId) {
        pageQueryService.deletePage(pageId);
        return ApiResponse.onSuccess(null);
    }
}
