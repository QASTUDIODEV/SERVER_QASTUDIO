package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.converter.ProjectConverter;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse.ProjectCreation;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.project.entity.enums.Role;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.project.repository.UserProject.UserProjectRepository;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommandServiceImpl implements ProjectCommandService{

    private final ProjectRepository projectRepository;
    private final ProjectConverter projectConverter;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

    @Override
    public ProjectCreation createProject(Long userId, ProjectRequest.CreateProject createProject) {
        Project newProject = projectConverter.toProject(createProject);

        Project savedProject = projectRepository.save(newProject);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.USER_NOT_FOUND));

        UserProject userProject = UserProject.builder().user(user).project(savedProject).role(Role.LEADER).userEmail(null).build();

        userProjectRepository.save(userProject);

        return projectConverter.toProjectCreationResponse(userProject, savedProject);
    }
}
