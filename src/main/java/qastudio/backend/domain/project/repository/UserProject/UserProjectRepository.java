package qastudio.backend.domain.project.repository.UserProject;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.UserProject;

import java.util.Optional;

public interface UserProjectRepository extends JpaRepository<UserProject, Long>, UserProjectRepositoryCustom {
    Optional<UserProject> findByUserIdAndProjectId(Long userId, Long projectId);
}
