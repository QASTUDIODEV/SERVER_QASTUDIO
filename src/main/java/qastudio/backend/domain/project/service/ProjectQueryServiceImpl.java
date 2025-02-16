package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.controller.ProjectController;
import qastudio.backend.domain.project.converter.ProjectConverter;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryServiceImpl implements ProjectQueryService {

    private final UserProjectRepository userProjectRepository;
    private final ProjectRepository projectRepository;


    @Override
    public ProjectResponse.ProjectDetail getSummarizedProjectInfo(Long projectId, Long userId) {
        boolean invitedStatus = userProjectRepository.existsByUserIdAndProjectId(userId, projectId);

        // 가입되어있지 않은 프로젝트를 조회할 경우 예외
        if(!invitedStatus) {
            throw new BadRequestException(ErrorStatus.UNAUTHORIZED_PROJECT);
        }

        // 프로젝트 조회
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        // 현재 사용자의 역할 조회
        UserProject userProject = userProjectRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_TEAM_MEMBER));

        boolean isLeader = userProject.getRole().equals(Role.LEADER);

        return ProjectConverter.toProjectDetail(project, isLeader);
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
}
