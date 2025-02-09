package qastudio.backend.domain.test.service;

import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;
import qastudio.backend.domain.test.repository.TestRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestQueryServiceImpl implements TestQueryService{
    private final ProjectRepository projectRepository;
    private final TestRepository testRepository;

    @Override
    public Page<Test> getTestList(Long projectId, Integer page, String testName, LocalDate date, String pageName, State state) {
        Project project = projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));

        Page<Test> testPage = testRepository.findAllByProject(
                project,
                testName,
                date,
                pageName,
                state,
                PageRequest.of(page, 6));

        return testPage;
    }

    @Override
    public Project getTestStatistics(Long projectId) {
        return projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));
    }

    @Override
    public List<Tuple> getTestCounts(Long projectId) {
        return testRepository.countTestsByProject(projectId);
    }

    // 전날 대비 성공률
    @Override
    public Double getSuccessRate(List<Tuple> testCounts) {
        return calculateRate(testCounts, State.SUCCESS);
    }

    // 전날 대비 실패율
    @Override
    public Double getFailRate(List<Tuple> testCounts) {
        return calculateRate(testCounts, State.FAIL);
    }

    private Double calculateRate(List<Tuple> counts, State state) {
        Long todayTotal = 0L, todayStateCount = 0L;
        Long yesterdayTotal = 0L, yesterdayStateCount = 0L;

        for (Tuple tuple : counts) {
            LocalDate date = tuple.get(0, LocalDate.class);

            Long total = tuple.get(1, Number.class) != null ? tuple.get(1, Number.class).longValue() : 0L;
            Long stateCount = (state == State.SUCCESS
                    ? tuple.get(2, Number.class)
                    : tuple.get(3, Number.class)) != null
                    ? (state == State.SUCCESS
                    ? tuple.get(2, Number.class).longValue()
                    : tuple.get(3, Number.class).longValue())
                    : 0L;

            if (date.equals(LocalDate.now())) {
                todayTotal = total;
                todayStateCount = stateCount;
            } else if (date.equals(LocalDate.now().minusDays(1))) {
                yesterdayTotal = total;
                yesterdayStateCount = stateCount;
            }
        }

        // 오늘과 전날의 성공률/실패율 계산
        Double todayRate = (todayTotal > 0) ? (double) todayStateCount / todayTotal * 100 : 0.0;
        Double yesterdayRate = (yesterdayTotal > 0) ? (double) yesterdayStateCount / yesterdayTotal * 100 : 0.0;

        // 전날 대비 변화율 계산
        if (yesterdayRate == 0) {
            return 0.0;
        }

        return Math.round(((todayRate - yesterdayRate) / yesterdayRate) * 1000) / 10.0;
    }
}
