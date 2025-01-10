package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import qastudio.backend.domain.project.dto.request.PageRequest;
import qastudio.backend.domain.project.dto.response.PageResponse;
import qastudio.backend.domain.project.service.PageQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/page")
public class PageController {

    private final PageQueryService pageQueryService;

    @Operation(
            summary = "페이지 생성 API",
            description = "페이지를 생성합니다."
    )
    @PostMapping("/{projectId}")
    public ApiResponse<PageResponse.PageSummary> createPage(@PathVariable("projectId") Long projectId, @RequestBody @Valid PageRequest.createPage createPage) {
        PageResponse.PageSummary pageSummary = pageQueryService.createPage(projectId, createPage);
        return ApiResponse.onSuccess(pageSummary);
    }

    @Operation(
            summary = "페이지 삭제 API",
            description = "페이지를 삭제합니다."
    )
    @DeleteMapping("/{pageId}")
    public ApiResponse<Void>  deletePage(@PathVariable("pageId") Long pageId) {
        PageResponse.PageSummary deletePage = pageQueryService.deletePage(pageId);
        return ApiResponse.onSuccess(null);
    }
}
