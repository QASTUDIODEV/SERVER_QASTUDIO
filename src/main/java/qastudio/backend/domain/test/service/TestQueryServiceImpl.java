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
    public Long getTotalTests(Long projectId) {
        return testRepository.countByTestDateAndState(projectId, null, null);
    }

    // 성공 테스트 수
    public Long getTotalSuccessTests(Long projectId) {
        return testRepository.countByTestDateAndState(projectId, null, State.SUCCESS);
    }

    // 실패 테스트 수
    public Long getTotalFailTests(Long projectId) {
        return testRepository.countByTestDateAndState(projectId, null, State.FAIL);
    }

    // 전날 대비 성공률
    public Double getSuccessRate(Long projectId) {
        Long todayTests = testRepository.countByTestDateAndState(projectId, LocalDate.now(), null);
        Long todaySuccessTests = testRepository.countByTestDateAndState(projectId, LocalDate.now(), State.SUCCESS);

        Long yesterdayTests = testRepository.countByTestDateAndState(projectId, LocalDate.now().minusDays(1), null);
        Long yesterdaySuccessTests = testRepository.countByTestDateAndState(projectId, LocalDate.now().minusDays(1), State.SUCCESS);

        if (todayTests == 0) {
            return 0.0; // 오늘 테스트가 없으면 성공 비율은 0
        }

        if (yesterdayTests == 0) {
            return 0.0; // 어제 테스트가 없으면 성공 비율은 0
        }

        Double todaySuccessRate = (double) todaySuccessTests / todayTests * 100;
        Double yesterdaySuccessRate = (double) yesterdaySuccessTests / yesterdayTests * 100;

        return Math.round((todaySuccessRate - yesterdaySuccessRate) * 10) / 10.0;
    }

    // 전날 대비 실패율
    public Double getFailRate(Long projectId) {
        Long todayTests = testRepository.countByTestDateAndState(projectId, LocalDate.now(), null);
        Long todayFailTests = testRepository.countByTestDateAndState(projectId, LocalDate.now(), State.FAIL);

        Long yesterdayTests = testRepository.countByTestDateAndState(projectId, LocalDate.now().minusDays(1), null);
        Long yesterdayFailTests = testRepository.countByTestDateAndState(projectId, LocalDate.now().minusDays(1), State.FAIL);

        if (todayTests == 0) {
            return 0.0; // 오늘 테스트가 없으면 실패 비율은 0
        }

        if (yesterdayTests == 0) {
            return 0.0; // 어제 테스트가 없으면 실패 비율은 0
        }

        Double todayFailRate = (double) todayFailTests / todayTests * 100;
        Double yesterdayFailRate = (double) yesterdayFailTests / yesterdayTests * 100;

        return Math.round((todayFailRate - yesterdayFailRate) * 10) / 10.0;
    }

    @Override
    public TestResponse.TestList searchTestsByTestName(Long projectId, String testName, Integer page, LocalDate date, String pageName, State state) {
        return null;
    };
}
