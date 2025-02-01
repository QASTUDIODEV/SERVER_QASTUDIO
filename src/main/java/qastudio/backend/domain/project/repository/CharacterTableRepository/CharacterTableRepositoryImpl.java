package qastudio.backend.domain.project.repository.CharacterTableRepository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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

    @Override
    public Page<CharacterTable> findAllByProjectIdWithPage(Long projectId, Pageable pageable) {
        List<CharacterTable> characterTables = jpaQueryFactory
                .selectFrom(characterTable)
                .where(characterTable.project.id.eq(projectId))
                .orderBy(characterTable.updatedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = jpaQueryFactory
                .select(characterTable.count())
                .from(characterTable)
                .where(characterTable.project.id.eq(projectId));

        return PageableExecutionUtils.getPage(characterTables, pageable, total::fetchOne);
    }
}
