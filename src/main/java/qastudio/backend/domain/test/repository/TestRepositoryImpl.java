package qastudio.backend.domain.test.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;

import static qastudio.backend.domain.test.entity.QTest.test;

@Repository
@RequiredArgsConstructor
public class TestRepositoryImpl implements TestRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

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
