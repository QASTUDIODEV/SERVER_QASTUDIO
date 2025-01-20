package qastudio.backend.domain.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.scenario.entity.ActionTable;
import java.util.List;

public interface ActionTableRepository extends JpaRepository<ActionTable, Long> {
    List<ActionTable> findByScenarioId(Long scenarioId);
}

