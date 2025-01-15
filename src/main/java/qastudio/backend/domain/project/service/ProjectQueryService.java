package qastudio.backend.domain.project.service;

import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;

import java.util.List;

public interface ProjectQueryService {
    ProjectResponse.ProjectDetail uploadProjectFile(Long projectId, MultipartFile zipFile);

    ProjectResponse.ProjectDetail getSummarizedProjectInfo(Long projectId);

    List<Project> getProjectList(Long userId);

    ProjectResponse.ProjectDetail updateProjectIntroduction(Long projectId, ProjectRequest.UpdateIntroduce updateIntroduce);

    ProjectResponse.ProjectDetail createProject(ProjectRequest.CreateProject createProject);
}
