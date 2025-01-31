package qastudio.backend.domain.test.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;
import qastudio.backend.domain.test.entity.Error;
import qastudio.backend.domain.test.entity.QError;
import qastudio.backend.domain.test.entity.QTest;

import java.util.Optional;

import static qastudio.backend.domain.test.entity.QError.error;
import static qastudio.backend.domain.test.entity.QTest.test;

@Repository
@RequiredArgsConstructor
public class ErrorRepositoryImpl implements ErrorRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Long saveErrorAndGetId(Integer errorCode, String errorMessage, String errorImage, Long testId) {
        Error errorEntity = Error.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .errorImage(errorImage)
                .build();

        entityManager.persist(errorEntity);
        entityManager.flush();  // DB 반영
        Long errorId = errorEntity.getId();

        queryFactory.update(test)
                .where(test.id.eq(testId))
                .set(test.error, errorEntity)
                .execute();

        return errorId;
    }

    @Override
    public Optional<Error> findByTestId(Long testId) {
        Error resultError = queryFactory
                .selectFrom(error)
                .join(error.test, test).fetchJoin()
                .where(test.id.eq(testId))
                .fetchOne();
        return Optional.ofNullable(resultError);
    }
}
