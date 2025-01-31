package qastudio.backend.domain.test.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qastudio.backend.domain.test.entity.Error;
import qastudio.backend.domain.test.entity.Test;

import java.util.Optional;

import static qastudio.backend.domain.test.entity.QError.error;
import static qastudio.backend.domain.test.entity.QTest.test;

@Repository
@RequiredArgsConstructor
public class ErrorRepositoryImpl implements ErrorRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Error saveError(Integer errorCode, String errorMessage, String errorImage, Long testId) {
        qastudio.backend.domain.test.entity.Error error = qastudio.backend.domain.test.entity.Error.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorImage(errorImage)
                .test(Test.builder().id(testId).build())  // testId 설정
                .build();
        entityManager.persist(error);
        return error;
    }
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
