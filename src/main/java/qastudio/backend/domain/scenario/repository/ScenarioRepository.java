package qastudio.backend.domain.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.scenario.entity.Scenario;

import java.util.List;

public interface ScenarioRepository extends JpaRepository<Scenario, Long>, ScenarioRepositoryCustom {
    List<Scenario> findAllByCharacterTableId(Long characterId);
}