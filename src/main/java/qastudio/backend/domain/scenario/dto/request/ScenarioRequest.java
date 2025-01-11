package qastudio.backend.domain.scenario.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
public class ScenarioRequest {

    @Getter
    @NoArgsConstructor
    public static class CreateScenarioRequest {

        @Schema(description = "역할 ID", example = "1")
        @NotNull(message = "캐릭터 ID는 필수 값입니다.")
        private Long characterId;

        @Schema(description = "시나리오 이름", example = "Login Test Scenario")
        @NotEmpty(message = "시나리오 이름은 필수 값입니다.")
        private String scenarioName;

        @Schema(description = "시나리오 설명", example = "이 시나리오는 로그인 동작을 테스트합니다.")
        private String scenarioDescription;

        @ArraySchema(
                arraySchema = @Schema(description = "액션 리스트"),
                schema = @Schema(implementation = Action.class)
        )
        @NotNull(message = "액션 리스트는 필수 값입니다.")
        private List<Action> actions;

        @Getter
        @NoArgsConstructor
        public static class Action {

            @Schema(description = "액션 이름", example = "Click Login Button")
            @NotEmpty(message = "액션 이름은 필수 값입니다.")
            private String actionName;

            @Schema(description = "액션 종류", example = "click")
            @NotEmpty(message = "액션 종류는 필수 값입니다.")
            private String actionType;

            @Schema(description = "액션 단계", example = "1")
            @NotNull(message = "액션 단계는 필수 값입니다.")
            private Integer step;
        }
    }
}
