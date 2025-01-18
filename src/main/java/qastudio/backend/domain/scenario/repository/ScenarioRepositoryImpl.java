package qastudio.backend.domain.scenario.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.scenario.entity.QScenario;
import qastudio.backend.domain.scenario.entity.Scenario;

@Repository
@RequiredArgsConstructor
public class ScenarioRepositoryImpl implements ScenarioRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Scenario> findAllById(List<Long> ids) {
        QScenario qScenario = QScenario.scenario;

        return jpaQueryFactory
                .selectFrom(qScenario)
                .where(qScenario.id.in(ids))
                .fetch();
    }
}
