package qastudio.backend.domain.test.service;

import qastudio.backend.domain.test.dto.response.ErrorResponse;

public interface ErrorQueryService {
    ErrorResponse.Error getError(Long testId);
}
