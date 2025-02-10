package qastudio.backend.domain.test.service;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;

import java.time.LocalDate;
import java.util.List;

public interface TestQueryService {
    Page<Test> getTestList(Long projectId, Integer page, String testName, LocalDate date, String pageName, State state);

    Project getProjectDetail(Long projectId);

    List<Tuple> getTestCounts(Long projectId);

    Double getSuccessRate(List<Tuple> testCounts);

    Double getFailRate(List<Tuple> testCounts);
}
