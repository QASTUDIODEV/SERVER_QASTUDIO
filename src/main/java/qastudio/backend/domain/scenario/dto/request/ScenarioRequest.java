package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

public class ScenarioRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateScenarioRequest {

        @Schema(example = "1", description = "캐릭터 ID")
        @NotNull(message = "Character ID는 필수입니다.")
        private Long characterId;

        @Schema(example = "1", description = "페이지 ID")
        @NotNull(message = "Page ID는 필수입니다.")
        private Long pageId;

        @Schema(example = "Login Test", description = "시나리오 이름")
        @NotBlank(message = "Scenario 이름은 필수입니다.")
        private String scenarioName;

        @Schema(example = "로그인 기능 테스트 시나리오", description = "시나리오 설명")
        @NotBlank(message = "Scenario 설명은 필수입니다.")
        private String scenarioDescription;

        @Schema(description = "액션 리스트")
        @NotNull(message = "Actions 리스트는 필수입니다.")
        private List<ActionRequest> actions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateScenarioRequest {
        @Schema(example = "Login Test", description = "시나리오 이름")
        @NotBlank(message = "Scenario 이름은 필수입니다.")
        private String scenarioName;

        @Schema(example = "로그인 기능 테스트 시나리오", description = "시나리오 설명")
        @NotBlank(message = "Scenario 설명은 필수입니다.")
        private String scenarioDescription;

        @Schema(description = "액션 리스트")
        @NotNull(message = "Actions 리스트는 필수입니다.")
        @Valid
        private List<ActionRequest> actions;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionRequest {

        @Schema(example = "Enter Email", description = "액션 설명")
        @NotBlank(message = "Action 설명은 필수입니다.")
        private String actionDescription;

        @Schema(example = "1", description = "실행 순서")
        @NotNull(message = "Step은 필수입니다.")
        private Integer step;

        @Schema(example = "input", description = "액션 타입")
        @NotBlank(message = "Action 타입은 필수입니다.")
        private String actionType;

        @Schema(example = "{\"strategy\": \"id\", \"value\": \"email\"}", description = "요소 선택자")
        @NotNull(message = "Locator 정보는 필수입니다.")
        private Map<String, Object> locator;

        @Schema(example = "{\"type\": \"send_keys\", \"value\": \"testuser@example.com\"}", description = "액션 정보")
        @NotNull(message = "Action 정보는 필수입니다.")
        private Map<String, Object> action;
    }

    @Getter
    @NoArgsConstructor
    public static class DeleteScenarios {

        @Schema(description = "삭제할 시나리오 ID 목록", example = "[1, 2, 3]")
        @NotEmpty(message = "삭제할 시나리오 ID 목록은 비어 있을 수 없습니다.")
        private List<Long> scenarioIds;
    }

}
