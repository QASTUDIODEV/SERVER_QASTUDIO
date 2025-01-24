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

        // 사용자 ID가 존재하는 경우 해당 Feature 조회
        Feature userFeature = jpaQueryFactory
                .selectFrom(feature)
                .where(
                        feature.action.id.eq(actionId),
                        feature.user.id.eq(userId)
                )
                .fetchOne();

        if (userFeature != null) {
            return Optional.of(userFeature);
        }

        // 사용자의 Feature가 없으면 기본 Feature(userId = null) 조회
        Feature defaultFeature = jpaQueryFactory
                .selectFrom(feature)
                .where(
                        feature.action.id.eq(actionId),
                        feature.user.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(defaultFeature);
    }

}
