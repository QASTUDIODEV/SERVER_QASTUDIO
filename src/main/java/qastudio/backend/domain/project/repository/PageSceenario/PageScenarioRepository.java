package qastudio.backend.domain.project.repository.PageSceenario;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.project.entity.PageScenario;

public interface PageScenarioRepository extends JpaRepository<PageScenario, Long>, PageScenarioRepositoryCustom {
}
