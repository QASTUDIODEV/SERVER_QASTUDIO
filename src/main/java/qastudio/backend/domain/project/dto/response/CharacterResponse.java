package qastudio.backend.domain.project.dto.response;

import java.time.LocalDateTime;
import lombok.*;

import java.util.List;

public class CharacterResponse {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectCharacter {
        // 역할 pk
        private Long characterId;
        // 역할 이름
        private String characterName;
        // 역할 설명
        private String characterDescription;
        // 역할에 등록 가능한 접근 권한 개수
        private Integer accessRightCnt;
        // 역할 시나리오 개수
        private Integer roleScenarioCnt;
        // 역할에 등록 가능한 접근 권한 리스트
        private List<String> accessRightList;
        // 시나리오 리스트
        private List<String> scenarioList;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectCharacterList {
        // 역할 리스트
        private List<ProjectCharacter> projectCharacters;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DetailCharacter {
        // 역할 detail
        private Long characterId;
        private String characterName;
        private String author;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DetailCharacterList {
        // detail 역할 리스트
        private List<DetailCharacter> detailCharacters;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Scenario {
        // 개별 시나리오
        private Long scenarioId;
        private String scenarioName;
        private String author;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ScenarioList {
        // 시나리오 리스트
        private List<Scenario> scenarioList;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CharacterScenario {
        // 역할 생성/수정 시에 받는 시나리오
        private Long characterId;
        private String characterName;
        private String characterDescription;
        private List<String> accessPage;
        private Long scenarioId;
        private String scenarioDescription;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PageInfo {
        private Long pageId;
        private String path;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProjectPathList {
        private List<PageInfo> projectPaths;
    }
}
