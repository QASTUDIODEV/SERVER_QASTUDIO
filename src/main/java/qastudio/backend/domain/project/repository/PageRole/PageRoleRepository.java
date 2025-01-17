package qastudio.backend.domain.project.repository.PageRole;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.PageRole;

public interface PageRoleRepository extends JpaRepository<PageRole, Long>, PageRoleRepositoryCustom {
}
