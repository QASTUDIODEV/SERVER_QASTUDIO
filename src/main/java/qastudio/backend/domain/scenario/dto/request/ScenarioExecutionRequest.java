package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScenarioExecutionRequest {

    @Schema(description = "테스트 대상 URL", example = "https://example.com")
    @NotEmpty(message = "URL은 필수입니다.")
    private String targetUrl;

    @Schema(description = "WebSocket 세션 ID", example = "user123")
    @NotEmpty(message = "세션 ID는 필수입니다.")
    private String sessionId;

    @Schema(description = "실행할 액션 목록")
    @NotNull(message = "액션 리스트는 필수 값입니다.")
    private List<Action> actions;

    @Getter
    @NoArgsConstructor
    public static class Action {

        @Schema(description = "액션 이름", example = "Click Login Button")
        @NotEmpty(message = "액션 이름은 필수 값입니다.")
        private String actionName;

        @Schema(description = "액션 단계", example = "1")
        @NotNull(message = "액션 단계는 필수 값입니다.")
        private Integer step;

        @Schema(description = "액션 내 요소 리스트")
        private List<Element> elements;

        @Getter
        @NoArgsConstructor
        public static class Element {

            @Schema(description = "요소 이름", example = "email_input")
            private String name;

            @Schema(description = "요소 타입", example = "input")
            private String type;

            @Schema(description = "요소 찾기 전략 및 값")
            private Locator locator;

            @Schema(description = "요소에 수행할 액션")
            private ActionDetails action;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Locator {
        @Schema(description = "Locator 전략", example = "css_selector")
        private String strategy;

        @Schema(description = "Locator 값", example = "#email")
        private String value;
    }

    @Getter
    @NoArgsConstructor
    public static class ActionDetails {
        @Schema(description = "액션 종류", example = "send_keys")
        private String type;

        @Schema(description = "입력 값", example = "testuser@example.com")
        private String value;
    }
}
