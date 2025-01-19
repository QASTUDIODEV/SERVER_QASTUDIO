package qastudio.backend.domain.project.repository.PageRole;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.PageRole;
import static qastudio.backend.domain.project.entity.QPageRole.pageRole;

import java.util.List;
import qastudio.backend.domain.project.entity.QPageRole;

@Repository
@RequiredArgsConstructor
public class PageRoleRepositoryImpl implements PageRoleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PageRole> findAllByPageId(Long pageId) {
        return jpaQueryFactory
                .selectFrom(pageRole)
                .where(pageRole.page.id.eq(pageId))
                .fetch();
    }

    @Override
    public List<PageRole> findAllByCharacterId(Long characterId) {
        QPageRole pageRole = QPageRole.pageRole;

        return jpaQueryFactory
                .selectFrom(pageRole)
                .where(pageRole.characterTable.id.eq(characterId))
                .fetch();
    }
}
