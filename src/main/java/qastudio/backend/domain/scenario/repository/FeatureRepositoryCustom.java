package qastudio.backend.domain.scenario.repository;

import qastudio.backend.domain.scenario.entity.Feature;

import java.util.Optional;

public interface FeatureRepositoryCustom {
    Optional<Feature> findFeatureByUserOrDefault(Long userId, Long actionId);
}
