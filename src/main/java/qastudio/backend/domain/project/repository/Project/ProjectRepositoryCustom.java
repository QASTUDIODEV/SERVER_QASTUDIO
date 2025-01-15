package qastudio.backend.domain.project.repository.Project;

import qastudio.backend.domain.project.entity.Project;

import java.util.Optional;

public interface ProjectRepositoryCustom {
    Optional<Project> findByProjectId(Long projectId);
}
