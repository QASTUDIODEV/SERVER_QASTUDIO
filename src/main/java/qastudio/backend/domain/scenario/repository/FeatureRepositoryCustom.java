package qastudio.backend.domain.scenario.repository;

import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;

import java.util.Optional;

public interface FeatureRepositoryCustom {
    Optional<Feature> findByActionAndUserIsNull(ActionTable action);
    Optional<Feature> findFeatureByUserOrDefault(Long userId, Long actionId);
    Optional<Feature> findByUserAndActionOrDefault(Long userId, ActionTable action);
}
