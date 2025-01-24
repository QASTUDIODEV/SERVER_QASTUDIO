package qastudio.backend.domain.scenario.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.QFeature;

import java.util.Optional;

@RequiredArgsConstructor
public class FeatureRepositoryImpl implements FeatureRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Feature> findFeatureByUserOrDefault(Long userId, Long actionId) {
        QFeature feature = QFeature.feature;

        Feature userFeature = jpaQueryFactory
                .selectFrom(feature)
                .where(
                        feature.action.id.eq(actionId),
                        userId != null ? feature.user.id.eq(userId) : feature.user.isNull()
                )
                .fetchFirst();

        return Optional.ofNullable(userFeature);
    }
}
