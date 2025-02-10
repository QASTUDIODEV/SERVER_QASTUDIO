package qastudio.backend.domain.test.converter;

import org.springframework.data.domain.Page;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.project.entity.UserProject;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TestConverter {
    public static TestResponse.TestStatistics toTestStatistics(
            Project project,
            Double successRate,
            Double failRate) {

        Integer participant = project.getUserProjects() != null
                ? (int) project.getUserProjects().stream()
                .filter(Objects::nonNull)
                .map(UserProject::getUserEmail)
                .distinct()
                .count()
                : 0;

        List<Test> tests = project.getTests();

        Long totalTestCnt = tests != null ? tests.stream().filter(Objects::nonNull).count() : 0L;

        Long totalSuccessTestCnt = (tests != null) ?
                tests.stream()
                        .filter(Objects::nonNull)
                        .filter(test -> test.getState() == State.SUCCESS)
                        .count()
                : 0L;

        Long totalFailTestCnt = (tests != null) ?
                tests.stream()
                        .filter(Objects::nonNull)
                        .filter(test -> test.getState() == State.FAIL)
                        .count()
                : 0L;

        return TestResponse.TestStatistics.builder()
                .projectId(project.getId())
                .projectName(project.getProjectName())
                .projectImage(project.getProjectImage())
                .totalSuccessCnt(totalSuccessTestCnt)
                .successRate(successRate)
                .totalFailCnt(totalFailTestCnt)
                .failRate(failRate)
                .participant(participant)
                .totalTestCnt(totalTestCnt)
                .build();
    }

    public static TestResponse.Test toTest(Test test) {
        Integer attainment = (int) Math.round(
                (double) test.getExecutionActionCount() / test.getTotalActionCount() * 100 );

        TestResponse.Test.TestBuilder responseBuilder = TestResponse.Test.builder()
                .testId(test.getId())
                .testDate(test.getTestDate())
                .testName(test.getTestName())
                .pageName(test.getPage().getPageName())
                .attainment(attainment)
                .state(test.getState())
                .time(test.getTime())
                .nickname(test.getUser().getNickname());

        if (test.getState() == State.SUCCESS) {
            responseBuilder.scenarioRecord(test.getScenarioRecord());
        }

        if (test.getState() == State.FAIL) {
            responseBuilder.errorId(test.getError().getId());
        }

        return responseBuilder.build();
    }

    public static TestResponse.TestList toTestList(Page<Test> testList) {
        List<TestResponse.Test> testLists = testList.stream()
                .map(TestConverter::toTest).collect(Collectors.toList());

        return TestResponse.TestList.builder()
                .testList(testLists)
                .listSize(testLists.size())
                .totalPage(testList.getTotalPages())
                .totalElements(testList.getTotalElements())
                .offset(testList.getPageable().getOffset())
                .limit(testList.getPageable().getPageSize())
                .isFirst(testList.isFirst())
                .isLast(testList.isLast())
                .hasPrevious(testList.hasPrevious())
                .hasNext(testList.hasNext())
                .build();
    }
}
