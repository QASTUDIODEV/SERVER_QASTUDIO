package qastudio.backend.domain.scenario.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.scenario.entity.Scenario;
import static qastudio.backend.domain.scenario.entity.QScenario.scenario;
import static qastudio.backend.domain.project.entity.QCharacterTable.characterTable;

@Repository
@RequiredArgsConstructor
public class ScenarioRepositoryImpl implements ScenarioRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Scenario> findAllById(List<Long> ids) {
        return jpaQueryFactory
                .selectFrom(scenario)
                .where(scenario.id.in(ids))
                .fetch();
    }

    @Override
    public List<Scenario> findAllByCharacterId(Long characterId) {
        return jpaQueryFactory
                .selectFrom(scenario)
                .where(scenario.characterTable.id.eq(characterId))
                .fetch();
    }

    @Override
    public List<Scenario> findAllByCharacterIdIn(List<Long> characterIds) {
        return jpaQueryFactory
                .selectFrom(scenario)
                .join(scenario.characterTable, characterTable).fetchJoin()
                .where(characterTable.id.in(characterIds))
                .orderBy(scenario.updatedAt.desc())
                .fetch();
    }
}
