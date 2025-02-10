package qastudio.backend.domain.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.converter.ProjectConverter;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.service.ProjectCommandService;
import qastudio.backend.domain.project.service.ProjectQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;
import qastudio.backend.global.apiPayload.code.exception.custom.AuthException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;
import qastudio.backend.global.handler.annotation.Auth;

import java.util.List;
import qastudio.backend.global.s3.service.S3Service;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/projects")
public class ProjectController {

    private final ProjectQueryService projectQueryService;
    private final ProjectCommandService projectCommandService;

    @Operation(
            summary = "프로젝트 생성 API | by 챠리",
            description = "새로운 프로젝트를 생성합니다. 프로젝트 이미지는 presigned/upload로 업로드 후, response.result의 keyName만 projectImage로 주세요"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON201", description = "프로젝트 생성 성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PostMapping(value = "")
    public ApiResponse<ProjectResponse.ProjectCreation> createProject(@Auth Long userId, @RequestBody @Valid ProjectRequest.CreateProject createProject){
        ProjectResponse.ProjectCreation projectCreation = projectCommandService.createProject(userId, createProject);
        return ApiResponse.onSuccess(projectCreation);
    }

    @Operation(
            summary = "zip 파일 업로드 API | by 노을",
            description = "zip파일을 업로드하여 프로젝트 구조를 학습합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.")
    })
    @PostMapping(value = "/{projectId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProjectResponse.ProjectDetail> uploadProjectFile(
            @Auth Long userId,
            @PathVariable("projectId") Long projectId,
            @RequestParam("zipFile") MultipartFile zipFile,
            @Parameter(hidden = true) HttpServletRequest request) throws JsonProcessingException {

        // 쿠키에서 JWT 토큰 추출
        String jwtToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        // 토큰이 없을 경우 예외 처리
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new AuthException(ErrorStatus.MISSING_AUTHORITY);
        }

        Project project = projectCommandService.uploadProjectFile(userId, projectId, zipFile, jwtToken);
        return ApiResponse.onSuccess(ProjectConverter.toProjectDetail(project));
    }

    @Operation(
            summary = "프로젝트 요약 정보 조회 API | by 노을",
            description = "프로젝트의 요약 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
    })
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse.ProjectDetail> getSummarizedProjectInfo(@PathVariable("projectId") Long projectId, @Auth Long userId) {
        Project project = projectQueryService.getSummarizedProjectInfo(projectId, userId);
        return ApiResponse.onSuccess(ProjectConverter.toProjectDetail(project));
    }

    @Operation(
            summary = "프로젝트 리스트 조회 API | by 노을",
            description = "사이드바용 프로젝트 리스트를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
    })
    @GetMapping()
    public ApiResponse<ProjectResponse.ProjectList> getProjectList(@Auth Long userId) {
        List<Project> projects = projectQueryService.getProjectList(userId);
        return ApiResponse.onSuccess(ProjectConverter.toProjectList(projects));
    }

    @Operation(
            summary = "프로젝트 introduction 수정 API | by 노을",
            description = "프로젝트의 introduction 을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "The project does not exist.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PROJECT404",
                                    summary = "The project does not exist.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"PROJECT404\",\n  \"message\": \"The project does not exist.\"\n}"
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "Invalid request.",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "COMMON400",
                                    summary = "Invalid request.",
                                    value = "{\n  \"isSuccess\": false,\n  \"code\": \"COMMON400\",\n  \"message\": \"Invalid request.\"\n}"
                            )
                    )),
    })
    @PatchMapping("/{projectId}")
    public ApiResponse<ProjectResponse.ProjectDetail> updateProjectIntroduction(@PathVariable("projectId") Long projectId, @RequestBody @Valid ProjectRequest.UpdateIntroduce updateIntroduce) {
        Project project = projectCommandService.updateProjectIntroduction(projectId, updateIntroduce);
        return ApiResponse.onSuccess(ProjectConverter.toProjectDetail(project));
    }

}
