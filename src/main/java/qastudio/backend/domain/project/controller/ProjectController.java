package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.converter.ProjectConverter;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.service.ProjectQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v0/project")
public class ProjectController {

    private final ProjectQueryService projectQueryService;

    @Operation(
            summary = "프로젝트 생성 API",
            description = "새로운 프로젝트를 생성합니다."
    )
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProjectResponse.ProjectDetail> createProject(@RequestBody @Valid ProjectRequest.CreateProject createProject){
        ProjectResponse.ProjectDetail projectDetail = projectQueryService.createProject(createProject);
        return ApiResponse.onSuccess(projectDetail);
    }

    @Operation(
            summary = "zip 파일 업로드 API",
            description = "zip파일을 업로드하여 프로젝트 구조를 학습합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PostMapping(value = "/upload/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProjectResponse.ProjectDetail> uploadProjectFile(@PathVariable("projectId") Long projectId, @RequestParam("zipFile") MultipartFile zipFile) {
        ProjectResponse.ProjectDetail projectDetail = projectQueryService.uploadProjectFile(projectId, zipFile);
        return ApiResponse.onSuccess(projectDetail);
    }

    @Operation(
            summary = "프로젝트 요약 정보 조회 API",
            description = "프로젝트의 요약 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다.")
    })
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse.ProjectDetail> getSummarizedProjectInfo(@PathVariable("projectId") Long projectId) {
        ProjectResponse.ProjectDetail projectDetail = projectQueryService.getSummarizedProjectInfo(projectId);
        return ApiResponse.onSuccess(projectDetail);
    }

    @Operation(
            summary = "프로젝트 리스트 조회 API",
            description = "사이드바용 프로젝트 리스트를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
    })
    @GetMapping("/list")
    public ApiResponse<ProjectResponse.ProjectList> getProjectList() {
        List<Project> projects = projectQueryService.getProjectList();
        return ApiResponse.onSuccess(ProjectConverter.toProjectList(projects));
    }

    @Operation(
            summary = "프로젝트 introduction 수정 API",
            description = "프로젝트의 introduction 을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PROJECT404", description = "존재하지 않는 프로젝트입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON400", description = "잘못된 요청입니다.")
    })
    @PatchMapping("/{projectId}")
    public ApiResponse<ProjectResponse.ProjectDetail> updateProjectIntroduction(@PathVariable("projectId") Long projectId, @RequestBody @Valid ProjectRequest.UpdateIntroduce updateIntroduce) {
        System.out.println("Introduce: " + updateIntroduce.getIntroduce());

        ProjectResponse.ProjectDetail projectDetail = projectQueryService.updateProjectIntroduction(projectId, updateIntroduce);
        return ApiResponse.onSuccess(projectDetail);
    }

}
