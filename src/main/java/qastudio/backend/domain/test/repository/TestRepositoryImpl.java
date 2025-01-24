package qastudio.backend.domain.test.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;
import java.util.List;

import static qastudio.backend.domain.test.entity.QTest.test;

@Repository
@RequiredArgsConstructor
public class TestRepositoryImpl implements TestRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Test> findAllByProject(Project project, String testName, LocalDate date, String pageName, State state, PageRequest pageRequest) {
        BooleanExpression condition = test.project.eq(project);

        if (testName != null && !testName.isEmpty()) {
            condition = condition.and(test.testName.containsIgnoreCase(testName));
        }

        if (date != null) {
            condition = condition.and(test.testDate.eq(date));
        }

        if (pageName != null && !pageName.isEmpty()) {
            condition = condition.and(test.page.pageName.eq(pageName));
        }

        if (state != null) {
            condition = condition.and(test.state.eq(state));
        }

        List<Test> tests = jpaQueryFactory
                .select(test)
                .from(test)
                .where(condition)
                .orderBy(test.testDate.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        long totalCount = jpaQueryFactory
                .select(test.count())
                .from(test)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(tests, pageRequest, totalCount);
    }

    @Override
    public Long countByTestDateAndState(Long projectId, LocalDate testDate, State state) {
        return jpaQueryFactory
                .select(test.count())
                .from(test)
                .where(
                        test.project.id.eq(projectId)
                                .and(testDate != null ? test.testDate.eq(testDate) : null)
                                .and(state != null ? test.state.eq(state) : null)
                )
                .fetchOne();
    }
}
