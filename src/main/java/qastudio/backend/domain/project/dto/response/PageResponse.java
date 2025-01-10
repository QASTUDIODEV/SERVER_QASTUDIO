package qastudio.backend.domain.project.dto.response;

import lombok.*;

import java.util.List;

public class PageResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PageSummary {
        // 페이지 pk
        private Long pageId;
        // 페이지 이름
        private String pageName;
        // 페이지 설명
        private String pageDescription;
        // 페이지 경로
        private String path;
        // 페이지 접근 가능 권한
        private List<String> hasAccess;
        // 페이지 접근 불가능 권한
        private List<String> deniedAccess;
        // 시나리오
        private List<String> scenarios;
    }

}
