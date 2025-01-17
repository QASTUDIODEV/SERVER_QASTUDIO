package qastudio.backend.domain.project.repository.Page;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.Page;
import static qastudio.backend.domain.project.entity.QPage.page;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PageRepositoryImpl implements PageRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Page> findAllByProjectId(Long projectId) {
        return jpaQueryFactory
                .selectFrom(page)
                .where(page.project.id.eq(projectId))
                .fetch();
    }

    @Override
    public Optional<Page> findByPageId(Long pageId) {
        Page resultPage =  jpaQueryFactory
                .selectFrom(page)
                .where(page.id.eq(pageId))
                .fetchOne();
        return Optional.ofNullable(resultPage);
    }
}
