package qastudio.backend.domain.test.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import qastudio.backend.domain.project.entity.Page;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.repository.Page.PageRepository;
import qastudio.backend.domain.project.repository.Project.ProjectRepository;
import qastudio.backend.domain.test.dto.request.TestRequest;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;
import qastudio.backend.domain.test.repository.TestRepository;
import qastudio.backend.domain.user.entity.User;
import qastudio.backend.domain.user.repository.User.UserRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TestCommandServiceImpl implements TestCommandService {

    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final PageRepository pageRepository;
    @Override
    @Transactional
    public void updateTestErrorId(Long testId, Long errorId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found with id: " + testId));
        test.setErrorId(errorId);
    }

    @Transactional
    @Override
    public Long createTest(TestRequest testRequest) {
        User user = userRepository.findById(testRequest.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID: " + testRequest.getUserId()));

        Project project = projectRepository.findById(testRequest.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 프로젝트 ID: " + testRequest.getProjectId()));

        Page page = pageRepository.findById(testRequest.getPageId())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 페이지 ID: " + testRequest.getPageId()));

        Test test = Test.builder()
                .testDate(LocalDate.now())
                .testName(testRequest.getTestName())
                .attainment(testRequest.getAttainment())
                .state(testRequest.getState())
                .time(testRequest.getTime())
                .scenarioRecord(testRequest.getScenarioRecord())
                .totalActionCount(testRequest.getTotalActionCount())
                .executionActionCount(testRequest.getExecutionActionCount())
                .user(user)
                .project(project)
                .page(page)
                .build();

        Test savedTest = testRepository.save(test);
        return savedTest.getId();
    }
}
