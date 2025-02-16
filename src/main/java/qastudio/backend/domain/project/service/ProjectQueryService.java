package qastudio.backend.domain.project.service;
;
import qastudio.backend.domain.project.dto.response.ProjectResponse;
import qastudio.backend.domain.project.entity.Project;

import java.util.List;

public interface ProjectQueryService {
    ProjectResponse.ProjectDetail getSummarizedProjectInfo(Long projectId, Long userId);

    List<Project> getProjectList(Long userId);
}
