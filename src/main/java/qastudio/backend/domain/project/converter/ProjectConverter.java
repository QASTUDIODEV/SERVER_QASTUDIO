package qastudio.backend.domain.project.converter;

import java.util.ArrayList;
import org.springframework.stereotype.Component;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.request.TeamMemberRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.dto.response.ProjectResponse.ProjectCreation;
import qastudio.backend.domain.project.entity.Project;
import org.springframework.util.StringUtils;

import java.util.List;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.ViewType;
import qastudio.backend.global.s3.service.S3Service;

@Component
public class ProjectConverter {

    private final S3Service s3Service;

    public ProjectConverter(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public static ProjectResponse.ProjectDetail toProjectDetail(Project project, Boolean isLeader) {
        return ProjectResponse.ProjectDetail.builder()
                .projectId(project.getId())
                .projectImage(project.getProjectImage())
                .projectName(project.getProjectName())
                .projectUrl(project.getProjectUrl())
                .introduction(project.getIntroduction())
                .viewType(project.getViewType())
                .assistantId(project.getAssistantId())
                .developmentSkill(project.getDevelopmentSkill())
                .isLeader(isLeader)
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

    public Project toProject(ProjectRequest.CreateProject request) {
        String staticUrl = getStaticUrl(request.getProjectImage());

        return Project.builder()
                .projectName(request.getProjectName())
                .projectImage(staticUrl)
                .projectUrl(request.getProjectUrl())
                .build();
    }

    public String getStaticUrl (String projectImage) {
        if(StringUtils.hasText(projectImage)) {
            return s3Service.generateStaticUrl(projectImage);
        }
        return null;
    }

    public ProjectCreation toProjectCreationResponse(UserProject userProject, List<TeamMemberRequest.MemberEmail> memberEmailList, Project newProject) {
        return ProjectCreation.builder()
                .userId(userProject.getUser().getId())
                .projectId(newProject.getId())
                .projectName(newProject.getProjectName())
                .projectImage(newProject.getProjectImage())
                .projectUrl(newProject.getProjectUrl())
                .memberEmails(memberEmailList)
                .build();
    }
}
