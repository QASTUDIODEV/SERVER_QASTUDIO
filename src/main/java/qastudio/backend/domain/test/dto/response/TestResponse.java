package qastudio.backend.domain.test.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
        // 프로젝트 대표 이미지
        private String projectImage;
        // 총 성공 횟수
        private Long totalSuccessCnt;
        // 전날 대비 성공률
        private Double successRate;
        // 총 실패 횟수
        private Long totalFailCnt;
        // 전날 대비 실패율
        private Double failRate;
        // 프로젝트 참여자 수
        private Integer participant;
        // 전체 테스트 횟수
        private Long totalTestCnt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Test {
        // 테스트 pk
        private Long testId;
        // 테스트 날짜
        private LocalDate testDate;
        // 테스트 이름
        private String testName;
        // 페이지 이름
        private String pageName;
        // 성취도
        private Integer attainment;
        // 성취 여부
        private String state;
        // 소요 시간
        private Double time;
        // 닉네임
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TestList {
        // 테스트 리스트
        private List<Test> testList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }
}
