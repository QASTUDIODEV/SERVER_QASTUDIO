package qastudio.backend.domain.test.service;

import qastudio.backend.domain.test.dto.response.TestResponse;

public interface TestQueryService {
    TestResponse.TestStatistics getTestStatistics(Long projectId);
}
