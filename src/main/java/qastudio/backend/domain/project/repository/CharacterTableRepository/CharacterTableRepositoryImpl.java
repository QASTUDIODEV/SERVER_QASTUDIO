package qastudio.backend.domain.project.repository.CharacterTableRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.CharacterTable;
import qastudio.backend.domain.project.entity.QCharacterTable;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CharacterTableRepositoryImpl implements CharacterTableRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCharacterTable qCharacterTable = QCharacterTable.characterTable;

    @Override
    public List<CharacterTable> findAllByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(qCharacterTable)
                .where(qCharacterTable.project.id.eq(projectId))
                .fetch();
    }
}
