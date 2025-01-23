package qastudio.backend.domain.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;

import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByAction(ActionTable action);
}
