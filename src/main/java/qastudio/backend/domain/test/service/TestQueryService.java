package qastudio.backend.domain.test.service;

import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;

public interface TestQueryService {
    TestResponse.TestList getTestList(Long projectId, Integer page, String testName, LocalDate date, String pageName, State state);

    Project getTestStatistics(Long projectId);

    Long getTotalTests(Long projectId);

    Long getTotalSuccessTests(Long projectId);

    Long getTotalFailTests(Long projectId);

    Double getSuccessRate(Long projectId);

    Double getFailRate(Long projectId);
}
