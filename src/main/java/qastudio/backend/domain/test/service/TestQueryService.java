package qastudio.backend.domain.test.service;

import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;

public interface TestQueryService {
    TestResponse.TestList getTestList(Long projectId, Integer page, LocalDate date, String pageName, State state);

    TestResponse.TestStatistics getTestStatistics(Long projectId);

    TestResponse.TestList searchTestsByTestName(Long projectId, String testName, Integer page, LocalDate date, String pageName, State state);
}
