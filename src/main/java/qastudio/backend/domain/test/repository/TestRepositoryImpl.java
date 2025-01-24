package qastudio.backend.domain.test.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.test.entity.QTest;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;


@Repository
@RequiredArgsConstructor
public class TestRepositoryImpl implements TestRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;
    @PersistenceContext
    private EntityManager entityManager;

    private static final QTest test = QTest.test;

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
