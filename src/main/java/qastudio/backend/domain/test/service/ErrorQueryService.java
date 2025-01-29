package qastudio.backend.domain.test.service;

import qastudio.backend.domain.test.entity.Error;

public interface ErrorQueryService {
    Error getError(Long testId);
}
