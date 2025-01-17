package qastudio.backend.domain.project.converter;

import java.util.ArrayList;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.dto.response.ProjectResponse.ProjectCreation;
import qastudio.backend.domain.project.entity.Project;

import java.util.List;
import qastudio.backend.domain.project.entity.enums.ViewType;

@Component
public class ProjectConverter {

    public static ProjectResponse.ProjectDetail toProjectDetail(Project project) {
        return ProjectResponse.ProjectDetail.builder()
                .projectId(project.getId())
                .projectImage(project.getProjectImage())
                .projectName(project.getProjectName())
                .projectUrl(project.getProjectUrl())
                .introduction(project.getIntroduction())
                .viewType(project.getViewType())
                .build();
    }

    public static ProjectResponse.ProjectList toProjectList(List<Project> projects) {

        List<ProjectResponse.ProjectSummary> projectSummaries = projects.stream()
                .map(project -> ProjectResponse.ProjectSummary.builder()
                        .projectId(project.getId())
                        .projectImage(project.getProjectImage())
                        .projectName(project.getProjectName())
                        .build())
                .toList();

        return ProjectResponse.ProjectList.builder()
                .projectList(projectSummaries)
                .build();
        
    }

    public Project toEntity(ProjectRequest.CreateProject request, String projectImageUrl) {
        return Project.builder()
                .projectName(request.getProjectName())
                .projectImage(projectImageUrl)
                .projectUrl(request.getProjectUrl())
                .viewType(request.getViewType() != null ? request.getViewType() : ViewType.PC)
                .build();
    }

    public ProjectCreation toResponse(Project project) {
        return ProjectCreation.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .projectImage(project.getProjectImage())
                .projectUrl(project.getProjectUrl())
                .memberEmails(new ArrayList<>()) // 멤버 이메일은 필요에 따라 추가 로직 구현
                .build();
    }
}
