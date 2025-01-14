package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectQueryServiceImpl implements ProjectQueryService {

    private final UserProjectRepository userProjectRepository;
    private final ProjectRepository projectRepository;

    @Override
    public ProjectResponse.ProjectDetail uploadProjectFile(Long projectId, MultipartFile zipFile) {
        return null;
    }

    @Override
    public ProjectResponse.ProjectDetail getSummarizedProjectInfo(Long projectId) {

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        return ProjectResponse.ProjectDetail.builder()
                .projectId(projectId)
                .projectImage(project.getProjectImage())
                .projectName(project.getProjectName())
                .projectUrl(project.getProjectUrl())
                .introduction(project.getIntroduction())
                .viewType(project.getViewType())
                .build();
    }

    @Override
    public List<Project> getProjectList(Long userId) {
        // UserProject 리스트
        List<UserProject> userProjectList = userProjectRepository.findUserProjectsByUserId(userId);

        // Project 리스트로 변환
        return userProjectList.stream()
                .map(UserProject::getProject)
                .toList();
    }

    @Override
    public ProjectResponse.ProjectDetail updateProjectIntroduction(Long projectId, ProjectRequest.UpdateIntroduce updateIntroduce) {
        return null;
    }

    @Override
    public ProjectResponse.ProjectDetail createProject(ProjectRequest.CreateProject createProject){
        return null;
    }
}
