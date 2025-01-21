package qastudio.backend.domain.project.service;

import qastudio.backend.domain.project.dto.request.ProjectRequest;
import qastudio.backend.domain.project.dto.response.ProjectResponse.ProjectCreation;

public interface ProjectCommandService {
    ProjectCreation createProject( Long userId, ProjectRequest.CreateProject createProject);
}
