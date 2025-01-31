package qastudio.backend.domain.project.repository.CharacterTableRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.CharacterTable;
import static qastudio.backend.domain.project.entity.QCharacterTable.characterTable;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CharacterTableRepositoryImpl implements CharacterTableRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CharacterTable> findAllByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(characterTable)
                .where(characterTable.project.id.eq(projectId))
                .fetch();
    }

    @Override
    public List<CharacterTable> findAllById(List<Long> ids) {
        return jpaQueryFactory
                .selectFrom(characterTable)
                .where(characterTable.id.in(ids))
                .fetch();
    }

}
