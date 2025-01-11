package qastudio.backend.domain.test.dto.response;

import lombok.*;

public class TestResponse {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TestStatistics {
        // 프로젝트 pk
        private Long projectId;
        // 프로젝트 이름
        private String projectName;
        // 총 성공 횟수
        private Integer totalSuccessCnt;
        // 전날 대비 성공률
        private Double successRate;
        // 총 실패 횟수
        private Integer totalFailCnt;
        // 전날 대비 실패율
        private Double failRate;
        // 프로젝트 참여자 수
        private Integer participant;
        // 전체 테스트 횟수
        private Integer totalTestCnt;
    }
}