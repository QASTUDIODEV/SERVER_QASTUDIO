package qastudio.backend.domain.project.repository.Page;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.QPage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PageRepositoryImpl implements PageRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    private final QPage qPage = QPage.page;

    @Override
    public List<Page> findAllByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(qPage)
                .where(qPage.project.id.eq(projectId))
                .fetch();
    }
}
