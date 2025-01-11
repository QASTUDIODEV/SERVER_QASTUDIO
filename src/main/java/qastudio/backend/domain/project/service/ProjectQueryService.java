package qastudio.backend.domain.project.service;

import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;

public interface ProjectQueryService {
    ProjectResponse.ProjectDetail uploadProjectFile(Long projectId, MultipartFile zipFile);

    ProjectResponse.ProjectDetail getSummarizedProjectInfo(Long projectId);

    ProjectResponse.ProjectList getProjectList();

    ProjectResponse.ProjectDetail updateProjectIntroduction(Long projectId, ProjectRequest.UpdateIntroduce updateIntroduce);

    ProjectResponse.ProjectDetail createProject(ProjectRequest.CreateProject createProject);
}
