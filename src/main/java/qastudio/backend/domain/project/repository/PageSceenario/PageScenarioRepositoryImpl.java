package qastudio.backend.domain.project.repository.PageSceenario;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.PageScenario;
import qastudio.backend.domain.project.entity.QPage;
import qastudio.backend.domain.project.entity.QPageScenario;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PageScenarioRepositoryImpl implements PageScenarioRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPage qPage = QPage.page;
    private final QPageScenario qPageScenario = QPageScenario.pageScenario;

    @Override
    public List<PageScenario> findAllByPageId(Long pageId) {
        return jpaQueryFactory
                .selectFrom(qPageScenario)
                .where(qPageScenario.page.id.eq(pageId))
                .fetch();
    }
}
