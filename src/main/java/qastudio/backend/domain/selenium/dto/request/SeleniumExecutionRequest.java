package qastudio.backend.domain.selenium.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SeleniumExecutionRequest {
    @Schema(description = "테스트 대상 URL", example = "http://localhost:3000/")
    @NotEmpty(message = "URL은 필수입니다.")
    private String targetUrl;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "프로젝트 ID는 필수입니다.")
    private Long projectId;

    @NotNull(message = "페이지 ID는 필수입니다.")
    private Long pageId;

    @Schema(description = "실행할 액션 목록")
    @NotNull(message = "액션 리스트는 필수 값입니다.")
    private List<Action> actions;

    @Getter
    @NoArgsConstructor
    public static class Action {
        @Schema(description = "액션 이름", example = "Enter Email")
        @NotEmpty(message = "액션 이름은 필수 값입니다.")
        private String actionName;

        @Schema(description = "액션 단계", example = "1")
        @NotNull(message = "액션 단계는 필수 값입니다.")
        private Integer step;

        @Schema(description = "실행할 요소")
        @NotNull(message = "요소 정보는 필수 값입니다.")
        private Element element;
    }

    @Getter
    @NoArgsConstructor
    public static class Element {
        @Schema(description = "요소 이름", example = "email_input")
        private String name;

        @Schema(description = "요소 유형", example = "input")
        private String type;

        @Schema(description = "요소의 Locator 정보")
        @NotNull(message = "Locator 정보는 필수 값입니다.")
        private Locator locator;

        @Schema(description = "실행할 액션 정보")
        @NotNull(message = "액션 정보는 필수 값입니다.")
        private ActionDetail action;
    }

    @Getter
    @NoArgsConstructor
    public static class Locator {
        @Schema(description = "Locator 전략", example = "id")
        private String strategy;

        @Schema(description = "Locator 값", example = "email")
        private String value;
    }

    @Getter
    @NoArgsConstructor
    public static class ActionDetail {
        @Schema(description = "실행할 액션 유형", example = "send_keys")
        private String type;

        @Schema(description = "입력할 값 (SEND_KEYS의 경우)", example = "testuser@example.com")
        private String value;
    }
}
