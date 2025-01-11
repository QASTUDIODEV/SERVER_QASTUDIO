package qastudio.backend.domain.project.converter;

import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;

import java.util.List;

public class ProjectConverter {

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
}
