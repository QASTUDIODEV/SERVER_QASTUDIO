package qastudio.backend.domain.test.repository;

import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;

public interface TestRepositoryCustom {
    Long countByTestDateAndState(Long projectId, LocalDate testDate, State state);
}
