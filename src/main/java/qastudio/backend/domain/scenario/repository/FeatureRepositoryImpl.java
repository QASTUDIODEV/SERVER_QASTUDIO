package qastudio.backend.domain.scenario.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import qastudio.backend.domain.scenario.entity.ActionTable;
import qastudio.backend.domain.scenario.entity.Feature;
import qastudio.backend.domain.scenario.entity.QFeature;

import java.util.Optional;

@RequiredArgsConstructor
public class FeatureRepositoryImpl implements FeatureRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    @Override
    public Optional<Feature> findByUserAndActionOrDefault(Long userId, ActionTable action) {
        QFeature feature = QFeature.feature;

        Feature userFeature = jpaQueryFactory
                .selectFrom(feature)
                .where(
                        feature.action.eq(action),
                        feature.user.id.eq(userId) // 특정 사용자 Feature 조회
                )
                .fetchOne();

        if (userFeature != null) {
            return Optional.of(userFeature);
        }
        Feature defaultFeature = jpaQueryFactory
                .selectFrom(feature)
                .where(
                        feature.action.eq(action),
                        feature.user.isNull() // 기본 Feature 조회
                )
                .fetchOne();

        return Optional.ofNullable(defaultFeature);
    }

    @Override
    public Optional<Feature> findByActionAndUserIsNull(ActionTable action) {
        QFeature feature = QFeature.feature;

        Feature result = jpaQueryFactory
                .selectFrom(feature)
                .where(
                        feature.action.eq(action),
                        feature.user.isNull() // 기본 Feature만 조회 (userId = NULL)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
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
