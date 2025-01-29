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
@RequestMapping("/api/v0/projects")
public class PageController {

    private final PageQueryService pageQueryService;
    private final PageCommandService pageCommandService;

    @Operation(
            summary = "페이지 생성 API | by 노을",
            description = "페이지를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER400", description = "The role does not belong to the project.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "CHARACTER400",
                                    summary = "The role does not belong to the project.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"CHARACTER400\",\n  \"message\": \"The role does not belong to the project.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CHARACTER404", description = "The role does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "CHARACTER404",
                                    summary = "The role does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"CHARACTER404\",\n  \"message\": \"The role does not exist.\"\n}"
                            )
                    )),
    })
    @PostMapping("/{projectId}/pages")
    public ApiResponse<PageResponse.PageSummary> createPage(@PathVariable("projectId") Long projectId, @RequestBody @Valid PageRequest.createPage createPage) {
        Page page = pageCommandService.createPage(projectId, createPage);
        return ApiResponse.onSuccess(PageConverter.toPageSummary(page));
    }


    @Operation(
            summary = "프로젝트 페이지 조회 API | by 노을",
            description = "프로젝트에 해당하는 페이지를 모두 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "존재하지 않는 프로젝트입니다.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}/pages")
    public ApiResponse<PageResponse.PageList> getAllPage(@PathVariable("projectId") Long projectId) {
        List<Page> pages = pageQueryService.getAllPage(projectId);
        return ApiResponse.onSuccess(PageConverter.toPageList(pages));
    }

    @Operation(
            summary = "페이지 삭제 API | by 노을",
            description = "페이지를 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PAGE404", description = "The page does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PAGE404",
                                    summary = "The page does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PAGE404\",\n  \"message\": \"The page does not exist.\"\n}"
                            )
                    )),
    })
    @DeleteMapping("/pages/{pageId}")
    public ApiResponse<Void>  deletePage(@PathVariable("pageId") Long pageId) {
        pageCommandService.deletePage(pageId);
        return ApiResponse.onSuccess(null);
    }
}
