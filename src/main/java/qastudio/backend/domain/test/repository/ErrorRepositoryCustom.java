package qastudio.backend.domain.test.repository;

import qastudio.backend.domain.test.entity.Error;

import java.util.Optional;

public interface ErrorRepositoryCustom {
    Optional<Error> findByTestId(Long testId);
    Long saveErrorAndGetId(Integer errorCode, String errorMessage, String errorImage, Long testId);
}
