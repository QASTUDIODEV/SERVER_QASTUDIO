package qastudio.backend.domain.scenario.repository;

import java.util.List;
import qastudio.backend.domain.scenario.entity.Scenario;

public interface ScenarioRepositoryCustom {
    List<Scenario> findAllById(List<Long> ids);
    List<Scenario> findAllByCharacterId(Long characterId);
}
