package qastudio.backend.domain.project.service;
;
import qastudio.backend.domain.project.entity.Project;

import java.util.List;

public interface ProjectQueryService {
    Project getSummarizedProjectInfo(Long projectId);

    List<Project> getProjectList(Long userId);
}
