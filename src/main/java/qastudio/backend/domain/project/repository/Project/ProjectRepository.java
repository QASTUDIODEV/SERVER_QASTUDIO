package qastudio.backend.domain.project.repository.Project;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
}
