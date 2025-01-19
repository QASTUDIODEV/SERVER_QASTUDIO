package qastudio.backend.domain.project.repository.UserProject;

import qastudio.backend.domain.project.entity.UserProject;

import java.util.List;

public interface UserProjectRepositoryCustom {
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
    List<UserProject> findByProjectId(Long projectId);
    List<UserProject> findByUserId(Long userId);
}
