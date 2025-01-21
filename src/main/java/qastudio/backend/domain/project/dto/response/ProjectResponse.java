package qastudio.backend.domain.project.dto.response;

import lombok.*;
import qastudio.backend.domain.project.entity.enums.ViewType;

import java.util.List;

public class ProjectResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectList {
        // 프로젝트 리스트
        private List<ProjectSummary> projectList;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectSummary {
        // 프로젝트 pk
        private Long projectId;
        // 프로젝트 이미지
        private String projectImage;
        // 프로젝트 이름
        private String projectName;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectDetail {
        // 프로젝트 pk
        private Long projectId;
        // 프로젝트 이미지
        private String projectImage;
        // 프로젝트 이름
        private String projectName;
        // 프로젝트 url
        private String projectUrl;
        // 소개
        private String introduction;
        // 프로젝트 분류
        private ViewType viewType;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectCreation {
        private Long userId;
        private Long projectId;
        private String projectName;
        private String projectImage;
        private String projectUrl;
        private List<String> memberEmails;
    }
}
