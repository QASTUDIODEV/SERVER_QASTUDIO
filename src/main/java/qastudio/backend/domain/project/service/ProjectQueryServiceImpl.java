package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryServiceImpl implements ProjectQueryService {

    private final UserProjectRepository userProjectRepository;
    private final ProjectRepository projectRepository;


    @Override
    public Project getSummarizedProjectInfo(Long projectId) {

        // 프로젝트 조회
        return projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));
    }

    @Override
    public List<Project> getProjectList(Long userId) {
        // UserProject 리스트
        List<UserProject> userProjectList = userProjectRepository.findByUserId(userId);

        // Project 리스트로 변환
        return userProjectList.stream()
                .map(UserProject::getProject)
                .toList();
    }

    @Override
    public ProjectResponse.ProjectDetail createProject(ProjectRequest.CreateProject createProject){
        return null;
    }
}
