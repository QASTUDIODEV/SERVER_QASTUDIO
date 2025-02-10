package qastudio.backend.domain.test.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import java.util.Optional;

import static qastudio.backend.domain.project.entity.QProject.project;
import static qastudio.backend.domain.project.entity.QUserProject.userProject;
import static qastudio.backend.domain.test.entity.QTest.test;


@Repository
@RequiredArgsConstructor
public class TestRepositoryImpl implements TestRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @PersistenceContext
    private EntityManager entityManager;

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
    public Optional<Project> findAllByProjectId(Long projectId) {
        Project resultProject = jpaQueryFactory
                .selectFrom(project)
                .leftJoin(project.tests, test).fetchJoin()
                .leftJoin(project.userProjects, userProject).fetchJoin()
                .where(project.id.eq(projectId))
                .fetchOne();
        return Optional.ofNullable(resultProject);
    }


    @Override
    public List<Tuple> countTestsByProject(Long projectId) {
        return jpaQueryFactory
                .select(
                        test.testDate,
                        test.count(),
                        test.state.when(State.SUCCESS).then(1).otherwise(0).sum().coalesce(0),
                        test.state.when(State.FAIL).then(1).otherwise(0).sum().coalesce(0)
                )
                .from(test)
                .where(
                        test.project.id.eq(projectId),
                        test.testDate.in(LocalDate.now(), LocalDate.now().minusDays(1))
                )
                .groupBy(test.testDate)
                .fetch();
    }
}
