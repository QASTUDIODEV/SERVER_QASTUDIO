package qastudio.backend.domain.project.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.service.ProjectQueryService;
import qastudio.backend.global.apiPayload.ApiResponse;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {

    private final ProjectQueryService projectQueryService;

    @Operation(
            summary = "zip 파일 업로드 API",
            description = "zip파일을 업로드하여 프로젝트 구조를 학습합니다."
    )
    @PostMapping(value = "/upload/{projectId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ProjectResponse.ProjectDetail> uploadProjectFile(@PathVariable("projectId") Long projectId, @RequestParam("zipFile") MultipartFile zipFile) {
        ProjectResponse.ProjectDetail projectDetail = projectQueryService.uploadProjectFile(projectId, zipFile);
        return ApiResponse.onSuccess(projectDetail);
    }

    @Operation(
            summary = "프로젝트 요약 정보 조회 API",
            description = "프로젝트의 요약 정보를 조회합니다."
    )
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse.ProjectDetail> getSummarizedProjectInfo(@PathVariable("projectId") Long projectId) {
        ProjectResponse.ProjectDetail projectDetail = projectQueryService.getSummarizedProjectInfo(projectId);
        return ApiResponse.onSuccess(projectDetail);
    }

    @Operation(
            summary = "프로젝트 리스트 조회 API",
            description = "사이드바용 프로젝트 리스트를 조회합니다."
    )
    @GetMapping("/list")
    public ApiResponse<ProjectResponse.ProjectList> getProjectList() {
        ProjectResponse.ProjectList projectList = projectQueryService.getProjectList();
        return ApiResponse.onSuccess(projectList);
    }

    @Operation(
            summary = "프로젝트 introduction 수정 API",
            description = "프로젝트의 introduction 을 수정합니다."
    )
    @PatchMapping("/{projectId}")
    public ApiResponse<ProjectResponse.ProjectDetail> updateProjectIntroduction(@PathVariable("projectId") Long projectId, @RequestBody @Valid ProjectRequest.UpdateIntroduce updateIntroduce) {
        System.out.println("Introduce: " + updateIntroduce.getIntroduce());

        ProjectResponse.ProjectDetail projectDetail = projectQueryService.updateProjectIntroduction(projectId, updateIntroduce);
        return ApiResponse.onSuccess(projectDetail);
    }

}
