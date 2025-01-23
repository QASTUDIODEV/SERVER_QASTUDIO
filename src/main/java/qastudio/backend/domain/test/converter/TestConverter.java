package qastudio.backend.domain.test.converter;

import qastudio.backend.domain.project.entity.Project;
import qastudio.backend.domain.test.dto.response.TestResponse;

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
}
