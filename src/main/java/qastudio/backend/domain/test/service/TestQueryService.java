package qastudio.backend.domain.test.service;

import org.springframework.data.domain.Page;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;

public interface TestQueryService {
    Page<Test> getTestList(Long projectId, Integer page, String testName, LocalDate date, String pageName, State state);

    Project getTestStatistics(Long projectId);

    Long getTotalTests(Long projectId);

    Long getTotalSuccessTests(Long projectId);

    Long getTotalFailTests(Long projectId);

    Double getSuccessRate(Long projectId);

    Double getFailRate(Long projectId);
}
