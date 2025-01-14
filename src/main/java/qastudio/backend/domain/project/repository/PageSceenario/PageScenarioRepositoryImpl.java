package qastudio.backend.domain.project.repository.PageSceenario;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PageScenarioRepositoryImpl implements PageScenarioRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

}
