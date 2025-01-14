package qastudio.backend.domain.project.repository.UserProject;

import qastudio.backend.domain.project.entity.UserProject;

import java.util.List;

public interface UserProjectRepositoryCustom {
    boolean existsByUserAndProject(Long userId, Long projectId);
    List<UserProject> findUserProjectsByProjectId(Long projectId);
    List<UserProject> findUserProjectsByUserId(Long userId);
}
