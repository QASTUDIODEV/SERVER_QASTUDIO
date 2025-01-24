package qastudio.backend.domain.test.converter;

import org.springframework.data.domain.Page;
import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.dto.response.TestResponse;
import qastudio.backend.domain.test.entity.Test;
import qastudio.backend.domain.test.entity.enums.State;

import java.util.List;
import java.util.stream.Collectors;

public class TestConverter {
    public static TestResponse.TestStatistics toTestStatistics(
            Project project,
            Long totalTestCnt,
            Long totalSuccessTestCnt,
            Long totalFailTestCnt,
            Double successRate,
            Double failRate) {

        Integer participant = project.getUserProjects().size();

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
                .isFirst(testList.isFirst())
                .isLast(testList.isLast())
                .build();
    }
}
