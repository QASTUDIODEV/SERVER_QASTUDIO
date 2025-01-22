package qastudio.backend.domain.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.enums.State;
import qastudio.backend.domain.test.repository.TestRepository;
import qastudio.backend.global.apiPayload.code.exception.custom.BadRequestException;
import qastudio.backend.global.apiPayload.code.status.ErrorStatus;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestQueryServiceImpl implements TestQueryService{
    private final ProjectRepository projectRepository;
    private final TestRepository testRepository;

    @Override
    public TestResponse.TestList getTestList(Long projectId, Integer page, LocalDate date, String pageName, State state) {
        return null;
    };

    @Override
    public Project getTestStatistics(Long projectId) {
        return projectRepository.findByProjectId(projectId)
                .orElseThrow(() -> new BadRequestException(ErrorStatus.PROJECT_NOT_FOUND));
    }

    // 전체 테스트 수
    @Override
    public Long getTotalTests(Long projectId) {
        return testRepository.countByTestDateAndState(projectId, null, null);
    }

    // 성공 테스트 수
    @Override
    public Long getTotalSuccessTests(Long projectId) {
        return testRepository.countByTestDateAndState(projectId, null, State.SUCCESS);
    }

    // 실패 테스트 수
    @Override
    public Long getTotalFailTests(Long projectId) {
        return testRepository.countByTestDateAndState(projectId, null, State.FAIL);
    }

    // 전날 대비 성공률
    @Override
    public Double getSuccessRate(Long projectId) {
        return calculateRate(projectId, State.SUCCESS);
    }

    // 전날 대비 실패율
    @Override
    public Double getFailRate(Long projectId) {
        return calculateRate(projectId, State.FAIL);
    }

    private Double calculateRate(Long projectId, State state) {
        Long todayTests = testRepository.countByTestDateAndState(projectId, LocalDate.now(), null);
        Long todayStateTests = testRepository.countByTestDateAndState(projectId, LocalDate.now(), state);

        Long yesterdayTests = testRepository.countByTestDateAndState(projectId, LocalDate.now().minusDays(1), null);
        Long yesterdayStateTests = testRepository.countByTestDateAndState(projectId, LocalDate.now().minusDays(1), state);

        if (todayTests == 0 || yesterdayTests == 0) {
            return 0.0;
        }

        Double todayRate = (double) todayStateTests / todayTests * 100;
        Double yesterdayRate = (double) yesterdayStateTests / yesterdayTests * 100;

        if (yesterdayRate == 0) {
            return 0.0;
        }

        Double rateChange = ((todayRate - yesterdayRate) / yesterdayRate) * 100;
        return Math.round(rateChange * 10) / 10.0;
    }

    @Override
    public TestResponse.TestList searchTestsByTestName(Long projectId, String testName, Integer page, LocalDate date, String pageName, State state) {
        return null;
    };
}
