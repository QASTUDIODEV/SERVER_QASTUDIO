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
import qastudio.backend.domain.project.service.PageCommandService;
import qastudio.backend.domain.project.service.PageQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/pages")
public class PageController {

    private final PageQueryService pageQueryService;
    private final PageCommandService pageCommandService;

    @Operation(
            summary = "페이지 생성 API | by 노을",
            description = "페이지를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "잘못된 요청입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"잘못된 요청입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER400", description = "프로젝트에 속하지 않는 역할입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "CHARACTER400",
                                    summary = "프로젝트에 속하지 않는 역할입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"CHARACTER400\",\n  \"message\": \"프로젝트에 속하지 않는 역할입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"존재하지 않는 프로젝트입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER404", description = "존재하지 않는 역할입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "CHARACTER404",
                                    summary = "존재하지 않는 역할입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"CHARACTER404\",\n  \"message\": \"존재하지 않는 역할입니다.\"\n}"
                            )
                    )),
    })
    @PostMapping("/{projectId}")
    public ApiResponse<Void> createPage(@PathVariable("projectId") Long projectId, @RequestBody @Valid PageRequest.createPage createPage) {
        pageCommandService.createPage(projectId, createPage);
        return ApiResponse.onSuccess(null);
    }


    @Operation(
            summary = "프로젝트 페이지 조회 API | by 노을",
            description = "프로젝트에 해당하는 페이지를 모두 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "잘못된 요청입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"잘못된 요청입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"존재하지 않는 프로젝트입니다.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}")
    public ApiResponse<PageResponse.PageList> getAllPage(@PathVariable("projectId") Long projectId) {
        List<PageResponse.PageSummary> pages = pageQueryService.getAllPage(projectId);
        return ApiResponse.onSuccess(PageConverter.toPageList(pages));
    }

    @Operation(
            summary = "페이지 삭제 API | by 노을",
            description = "페이지를 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "잘못된 요청입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"잘못된 요청입니다.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PAGE404", description = "존재하지 않는 페이지입니다.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PAGE404",
                                    summary = "존재하지 않는 페이지입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PAGE404\",\n  \"message\": \"존재하지 않는 페이지입니다.\"\n}"
                            )
                    )),
    })
    @DeleteMapping("/{pageId}")
    public ApiResponse<Void>  deletePage(@PathVariable("pageId") Long pageId) {
        pageCommandService.deletePage(pageId);
        return ApiResponse.onSuccess(null);
    }
}
