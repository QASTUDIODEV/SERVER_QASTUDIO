package qastudio.backend.domain.test.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.test.entity.Error;

import java.util.Optional;

import static qastudio.backend.domain.test.entity.QError.error;
import static qastudio.backend.domain.test.entity.QTest.test;

@Repository
@RequiredArgsConstructor
public class ErrorRepositoryImpl implements ErrorRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Error> findByTestId(Long testId) {
        Error resultError = jpaQueryFactory
                .selectFrom(error)
                .join(error.test, test).fetchJoin()
                .where(test.id.eq(testId))
                .fetchOne();
        return Optional.ofNullable(resultError);
    }
}
