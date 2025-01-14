package qastudio.backend.domain.project.repository.PageRole;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.PageRole;
import qastudio.backend.domain.project.entity.QPage;
import qastudio.backend.domain.project.entity.QPageRole;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PageRoleRepositoryImpl implements PageRoleRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPage qPage = QPage.page;
    private final QPageRole qPageRole = QPageRole.pageRole;

    @Override
    public List<PageRole> findAllByPageId(Long pageId) {
        return jpaQueryFactory
                .selectFrom(qPageRole)
                .where(qPageRole.page.id.eq(pageId))
                .fetch();
    }
}
