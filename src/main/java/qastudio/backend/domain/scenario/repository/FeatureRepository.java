package qastudio.backend.domain.scenario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import qastudio.backend.domain.scenario.entity.Feature;

public interface FeatureRepository extends JpaRepository<Feature, Long>, FeatureRepositoryCustom {
}
