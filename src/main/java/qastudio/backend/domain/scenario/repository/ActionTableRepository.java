package qastudio.backend.domain.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.scenario.entity.ActionTable;

public interface ActionTableRepository extends JpaRepository<ActionTable, Long> {
}

