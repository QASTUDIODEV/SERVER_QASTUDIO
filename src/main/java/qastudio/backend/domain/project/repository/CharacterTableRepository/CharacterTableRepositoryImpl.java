package qastudio.backend.domain.project.repository.CharacterTableRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.scenario.entity.Scenario;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import qastudio.backend.domain.project.entity.QCharacterTable;
import qastudio.backend.domain.scenario.entity.QScenario;

@Repository
@RequiredArgsConstructor
public class CharacterTableRepositoryImpl implements CharacterTableRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    private final QCharacterTable characterTable = QCharacterTable.characterTable;
    private final QScenario scenario = QScenario.scenario;

    @Override
    public List<CharacterTable> findAllByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(characterTable)
                .where(characterTable.project.id.eq(projectId))
                .fetch();
    }

    @Override
    public List<Scenario> findAllByCharacterTableId(Long characterTableId) {
        return jpaQueryFactory
                .selectFrom(scenario)
                .where(scenario.characterTable.id.eq(characterTableId))
                .fetch();
    }

    @Override
    public List<CharacterTable> findAllById(List<Long> ids) {
        QCharacterTable characterTable = QCharacterTable.characterTable;

        return jpaQueryFactory
                .selectFrom(characterTable)
                .where(characterTable.id.in(ids))
                .fetch();
    }


}
