package qastudio.backend.domain.project.repository.UserProject;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.UserProject;

public interface UserProjectRepository extends JpaRepository<UserProject, Long>, UserProjectRepositoryCustom {
}
