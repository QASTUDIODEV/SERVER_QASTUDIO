package qastudio.backend.domain.project.repository.CharacterTableRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.CharacterTable;

import java.util.List;
import qastudio.backend.domain.project.entity.QCharacterTable;

@Repository
@RequiredArgsConstructor
public class CharacterTableRepositoryImpl implements CharacterTableRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    private final QCharacterTable characterTable = QCharacterTable.characterTable;

    @Override
    public List<CharacterTable> findAllByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(characterTable)
                .where(characterTable.project.id.eq(projectId))
                .fetch();
    }
}
