package qastudio.backend.domain.project.repository.PageSceenario;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.PageScenario;
import static qastudio.backend.domain.project.entity.QPage.page;
import static qastudio.backend.domain.project.entity.QPageScenario.pageScenario;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PageScenarioRepositoryImpl implements PageScenarioRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PageScenario> findAllByPageId(Long pageId) {
        return jpaQueryFactory
                .selectFrom(pageScenario)
                .where(pageScenario.page.id.eq(pageId))
                .fetch();
    }
}
