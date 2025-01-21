package qastudio.backend.domain.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.converter.ProjectConverter;
import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse.ProjectCreation;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectCommandServiceImpl implements ProjectCommandService{

    private final ProjectRepository projectRepository;
    private final ProjectConverter projectConverter;
    private final TeamMemberCommandService teamMemberCommandService;

    @Override
    public ProjectCreation createProject(ProjectRequest.CreateProject createProject) {
        Project newProject = projectConverter.toProject(createProject);

        Project savedProject = projectRepository.save(newProject);

        return projectConverter.toProjectCreationResponse(savedProject);
    }
}
