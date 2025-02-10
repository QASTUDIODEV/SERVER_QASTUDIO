package qastudio.backend.domain.selenium.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class SeleniumExecutionRequest {

    @Schema(description = "테스트할 사이트의 기본 URL", example = "http://localhost:3000")
    private final String targetUrl;

    @Schema(description = "프로젝트 ID", example = "2")
    private final Long projectId;

    @Schema(description = "페이지 ID", example = "3")
    private final Long pageId;

    @Schema(description = "실행할 액션 목록")
    private final List<ActionDetail> actions;
    public SeleniumExecutionRequest(String targetUrl, Long projectId, Long pageId, List<ActionDetail> actions) {
        this.targetUrl = targetUrl;
        this.projectId = projectId;
        this.pageId = pageId;
        this.actions = actions;
    }
    @Getter
    @Builder
    public static class ActionDetail {
        @Schema(description = "액션 ID", example = "1")
        private final Long actionId;

        @Schema(description = "액션 설명", example = "Enter Email")
        private final String actionDescription;

        @Schema(description = "액션 단계", example = "1")
        private final Integer step;

        @Schema(description = "액션 타입", example = "input")
        private final String actionType;

        @Schema(description = "Locator 정보")
        private final Locator locator;

        @Schema(description = "실행할 액션 정보")
        private final Action action;
    }

    @Getter
    @Builder
    public static class Locator {
        @Schema(description = "Locator 전략", example = "id")
        private final String strategy;

        @Schema(description = "Locator 값", example = "email")
        private final String value;
    }

    @Getter
    @Builder
    public static class Action {
        @Schema(description = "액션 타입", example = "send_keys")
        private final String type;

        @Schema(description = "액션 값", example = "testuser@example.com")
        private final String value;
    }
}
