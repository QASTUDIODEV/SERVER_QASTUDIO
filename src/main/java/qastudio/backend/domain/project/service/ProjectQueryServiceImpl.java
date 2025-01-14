package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectQueryServiceImpl implements ProjectQueryService {
    private final UserProjectRepository userProjectRepository;

    @Override
    public ProjectResponse.ProjectDetail uploadProjectFile(Long projectId, MultipartFile zipFile) {
        return null;
    }

    @Override
    public ProjectResponse.ProjectDetail getSummarizedProjectInfo(Long projectId) {
        return null;
    }

    @Override
    public List<Project> getProjectList(Long userId) {
        // UserProject 리스트
        List<UserProject> userProjectList = userProjectRepository.findUserProjectsByUserId(userId);

        // Project 리스트로 변환
        return userProjectList.stream()
                .map(UserProject::getProject) // UserProject에서 Project 객체 가져오기
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
