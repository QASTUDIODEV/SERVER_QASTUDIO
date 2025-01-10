package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;

@Service
@RequiredArgsConstructor
public class ProjectQueryServiceImpl implements ProjectQueryService {
    @Override
    public ProjectResponse.ProjectDetail uploadProjectFile(Long projectId, MultipartFile zipFile) {
        return null;
    }

    @Override
    public ProjectResponse.ProjectDetail getSummarizedProjectInfo(Long projectId) {
        return null;
    }

    @Override
    public ProjectResponse.ProjectList getProjectList() {
        return null;
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
